package org.metplus.cruncher.resume

import org.metplus.cruncher.rating.ProcessCruncher
import org.slf4j.LoggerFactory

class ReCrunchAllResumes(
        private val resumeRepository: ResumeRepository,
        private val cruncherProcess: ProcessCruncher<Resume>) {
    fun <T> process(observer: ReCrunchAllResumesObserver<T>): T {
        logger.trace("::process()")
        var numberScheduled = 0
        resumeRepository.getAll().forEach {
            cruncherProcess.addWork(it)
            logger.trace("going to process: ${it.userId}")
            numberScheduled++
        }
        logger.info("Going to process $numberScheduled resumes")
        return observer.onSuccess(numberScheduled)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(ReCrunchAllResumes::class.java)
    }
}

interface ReCrunchAllResumesObserver<T> {
    fun onSuccess(numberScheduled: Int): T
}
