package org.metplus.cruncher.resume

import org.metplus.cruncher.job.Job
import org.metplus.cruncher.job.JobsRepository
import org.metplus.cruncher.rating.Matcher

class MatchWithJob(
        private val resumeRepository: ResumeRepository,
        private val jobsRepository: JobsRepository,
        private val matcher: Matcher<Resume, Job>
) {
    fun <T> process(jobId: String, observer: MatchWithJobObserver<T>): T {
        val job = jobsRepository.getById(jobId) ?: return observer.jobNotFound(jobId)
        val allResumes = matcher.matchInverse(job, resumeRepository.getAll())

        return if (allResumes.isEmpty())
            observer.noMatchFound(jobId)
        else
            observer.success(allResumes)
    }
}

interface MatchWithJobObserver<T> {
    fun jobNotFound(jobId: String): T
    fun noMatchFound(jobId: String): T
    fun success(matchedResumes: List<Resume>): T
}