package org.metplus.cruncher.job

import org.metplus.cruncher.rating.Matcher
import org.metplus.cruncher.resume.Resume
import org.metplus.cruncher.resume.ResumeRepository
import org.slf4j.LoggerFactory

class MatchWithResume(
        private val resumeRepository: ResumeRepository,
        private val jobsRepository: JobsRepository,
        private val matcher: Matcher<Resume, Job>
) {
    fun <T> process(resumeId: String, observer: MatchWithResumeObserver<T>): T {
        logger.trace("process($resumeId)")
        val resume = resumeRepository.getByUserId(resumeId) ?: return observer.resumeNotFound(resumeId)
        val allJobs = jobsRepository.getAll()
        val allMatches = matcher.match(resume, allJobs)
        return if (allMatches.isEmpty())
            observer.noMatches(resumeId)
        else
            observer.success(allMatches)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(MatchWithResume::class.java)
    }
}

interface MatchWithResumeObserver<T> {
    fun success(matchedJobs: List<Job>): T
    fun resumeNotFound(resumeId: String): T
    fun noMatches(resumeId: String): T
}