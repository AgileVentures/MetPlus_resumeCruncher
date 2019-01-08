package org.metplus.cruncher.canned.resume

import org.metplus.cruncher.resume.MatchWithJobObserver
import org.metplus.cruncher.resume.Resume
import org.metplus.cruncher.resume.ResumeRepository

class MatchWithJobCanned(
        val resumeRepository: ResumeRepository
) {
    fun <T> process(jobId: String, observer: MatchWithJobObserver<T>) : T {
        val resumeStars = doubleArrayOf(1.8, 4.1, 2.6, 4.9, 3.2, 1.8, 4.1, 2.6, 4.9, 3.2)
        try {
            jobId.toInt()
        } catch (_: NumberFormatException) {
            return observer.jobNotFound(jobId)
        }

        if((jobId.toInt() <= 0 || jobId.toInt() >= 11) && jobId.toInt() % 5 == 0)
            return observer.noMatchFound(jobId)

        var maxResumeToReturn = 9
        if (jobId.toInt() > 10 && jobId.toInt() % 5 != 0)
            maxResumeToReturn = 3

        val resumes = mutableListOf<Resume>()
        0.rangeTo(maxResumeToReturn).forEach {
            val resume = resumeRepository.getByUserId((it+1).toString())
            if(resume != null)
                resumes.add(resume.copy(starRating = resumeStars[it]))
        }

        return observer.success(resumes)
    }
}