package org.metplus.cruncher.resume

import org.metplus.cruncher.job.Job
import org.metplus.cruncher.job.JobsRepository
import org.metplus.cruncher.rating.Matcher
import org.metplus.cruncher.rating.MatcherList

class MatchWithJob(
        private val resumeRepository: ResumeRepository,
        private val jobsRepository: JobsRepository,
        private val matcherList: MatcherList
) {
    fun <T> process(jobId: String, observer: MatchWithJobObserver<T>): T {
        val job = jobsRepository.getById(jobId) ?: return observer.jobNotFound(jobId)
        val allResumes = mutableMapOf<String, List<Resume>>()
        var hasMatches = false
        matcherList.getMatchers().forEach {
            val matches = (it as Matcher<Resume, Job>).matchInverse(job, resumeRepository.getAll())
            allResumes[it.getName()] = matches
            hasMatches = hasMatches || matches.isNotEmpty()
        }

        return if (!hasMatches)
            observer.noMatchFound(jobId, allResumes)
        else
            observer.success(allResumes)
    }
}

interface MatchWithJobObserver<T> {
    fun jobNotFound(jobId: String): T
    fun noMatchFound(jobId: String, matchers: Map<String, List<Resume>>): T
    fun success(matchedResumes: Map<String, List<Resume>>): T
}