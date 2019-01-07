package org.metplus.cruncher.rating

import org.metplus.cruncher.job.Job
import org.metplus.cruncher.job.JobsRepository
import org.metplus.cruncher.resume.Resume
import org.metplus.cruncher.resume.ResumeRepository

class CompareResumeWithJob(
        private val jobsRepository: JobsRepository,
        private val resumeRepository: ResumeRepository,
        private val matcher: Matcher<Resume, Job>
) {
    fun <T> process(resumeId: String, jobId: String, observer: CompareResumeWithJobObserver<T>): T {
        val job = jobsRepository.getById(jobId) ?: return observer.onJobNotFound(jobId)
        val resume = resumeRepository.getByUserId(resumeId) ?: return observer.onResumeNotFound(resumeId)
        return observer.onSuccess(matcher.similarityRating(resume, job))
    }
}

interface CompareResumeWithJobObserver<T> {
    fun onJobNotFound(jobId: String): T
    fun onResumeNotFound(resumeId: String): T
    fun onSuccess(starsRating: Double): T
}
