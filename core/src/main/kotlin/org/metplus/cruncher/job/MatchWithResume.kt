package org.metplus.cruncher.job

import org.metplus.cruncher.rating.Matcher
import org.metplus.cruncher.rating.MatcherList
import org.metplus.cruncher.resume.Resume
import org.metplus.cruncher.resume.ResumeRepository
import org.slf4j.LoggerFactory

class MatchWithResume(
        private val resumeRepository: ResumeRepository,
        private val jobsRepository: JobsRepository,
        private val matchers: MatcherList
) {
    fun <T> process(resumeId: String, observer: MatchWithResumeObserver<T>): T {
        logger.trace("process($resumeId)")
        val resume = resumeRepository.getByUserId(resumeId) ?: return observer.resumeNotFound(resumeId)
        val allJobs = jobsRepository.getAll()
        val allMatches = mutableMapOf<String, List<Job>>()
        var totalMatches = 0
        matchers.getMatchers().forEach {
            allMatches[it.getName()] = (it as Matcher<Resume, Job>).match(resume, allJobs)
            totalMatches += allMatches[it.getName()]!!.size
        }
        return if (totalMatches == 0)
            observer.noMatches(resumeId, matchers.getMatchers().map { it.getName() })
        else
            observer.success(allMatches)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(MatchWithResume::class.java)
    }
}

interface MatchWithResumeObserver<T> {
    fun success(matchedJobs: Map<String, List<Job>>): T
    fun resumeNotFound(resumeId: String): T
    fun noMatches(resumeId: String, crunchers: List<String>): T
}