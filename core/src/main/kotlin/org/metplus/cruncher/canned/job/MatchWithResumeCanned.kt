package org.metplus.cruncher.canned.job

import org.metplus.cruncher.job.Job
import org.metplus.cruncher.job.JobsRepository
import org.metplus.cruncher.job.MatchWithResumeObserver
import org.metplus.cruncher.rating.MatcherList

class MatchWithResumeCanned(
        val jobsRepository: JobsRepository,
        val matchers: MatcherList
) {
    fun <T> process(resumeId: String, matchWithResumeObserver: MatchWithResumeObserver<T>): T {
        val jobStars = doubleArrayOf(1.8, 4.1, 2.6, 4.9, 3.2, 1.8, 4.1, 2.6, 4.9, 3.2)
        try {
            resumeId.toInt()
        } catch (_: NumberFormatException) {
            return matchWithResumeObserver.resumeNotFound(resumeId)
        }

        if ((resumeId.toInt() <= 0 || resumeId.toInt() >= 11) && resumeId.toInt() % 5 == 0)
            return matchWithResumeObserver.noMatches(
                    resumeId,
                    matchers.getMatchers().map { it.getName() }
            )

        var maxJobToReturn = 9
        if (resumeId.toInt() > 10 && resumeId.toInt() % 5 != 0)
            maxJobToReturn = 3

        val jobs = mutableListOf<Job>()
        0.rangeTo(maxJobToReturn).forEach {
            val job = jobsRepository.getById((it + 1).toString())
            if (job != null)
                jobs.add(job.copy(starRating = jobStars[it]))
        }

        return matchWithResumeObserver.success(matchers.getMatchers().map { it.getName() to jobs }.toMap())
    }
}