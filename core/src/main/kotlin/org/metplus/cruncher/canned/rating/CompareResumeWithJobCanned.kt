package org.metplus.cruncher.canned.rating

import org.metplus.cruncher.rating.CompareResumeWithJob
import org.metplus.cruncher.rating.CompareResumeWithJobObserver

class CompareResumeWithJobCanned(
        private val compareResumeWithJob: CompareResumeWithJob
) {
    fun <T> process(resumeId: String, jobId: String, observer: CompareResumeWithJobObserver<T>): T {
        try {
            jobId.toInt()
        } catch(_: NumberFormatException) {
            observer.onJobNotFound(jobId)
        }

        try {
            resumeId.toInt()
        } catch(_: java.lang.NumberFormatException) {
            observer.onResumeNotFound(resumeId)
        }

        val jobIdentifier = Integer.valueOf(jobId)
        val resumeIdentifier = Integer.valueOf(resumeId)
        val stars = doubleArrayOf(1.2, 1.3, 3.1, 4.4, 4.9)

        var starsId = -1
        if (resumeIdentifier == 1)
            starsId = jobIdentifier
        if (starsId < 0 || starsId >= stars.size)
            return compareResumeWithJob.process(resumeId, jobId, observer)

        return observer.onSuccess(stars[starsId])
    }
}