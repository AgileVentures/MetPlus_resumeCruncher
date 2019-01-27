package org.metplus.cruncher.canned.resume

import org.metplus.cruncher.rating.MatcherList
import org.metplus.cruncher.resume.MatchWithJobObserver
import org.metplus.cruncher.resume.Resume
import org.metplus.cruncher.resume.ResumeRepository

class MatchWithJobCanned(
        val resumeRepository: ResumeRepository,
        val matcherList: MatcherList
) {
    fun <T> process(jobId: String, observer: MatchWithJobObserver<T>): T {
        val resumeStars = doubleArrayOf(1.8, 4.1, 2.6, 4.9, 3.2, 1.8, 4.1, 2.6, 4.9, 3.2)
        try {
            jobId.toInt()
        } catch (_: NumberFormatException) {
            return observer.jobNotFound(jobId)
        }

        if ((jobId.toInt() <= 0 || jobId.toInt() >= 11) && jobId.toInt() % 5 == 0) {
            val resumes = mutableMapOf<String, List<Resume>>()
            matcherList.getMatchers().forEach {
                val matcherName = it.getName()
                resumes[matcherName] = mutableListOf()
            }
            return observer.noMatchFound(jobId, resumes)
        }

        var maxResumeToReturn = 9
        if (jobId.toInt() > 10 && jobId.toInt() % 5 != 0)
            maxResumeToReturn = 3

        val resumes = mutableMapOf<String, List<Resume>>()
        matcherList.getMatchers().forEach {
            val matcherName = it.getName()
            resumes[matcherName] = mutableListOf()
            0.rangeTo(maxResumeToReturn).forEach {
                val resume = resumeRepository.getByUserId((it + 1).toString())
                if (resume != null)
                    (resumes[matcherName] as MutableList).add(resume.copy(starRating = resumeStars[it]))
            }
        }

        return observer.success(resumes)
    }
}