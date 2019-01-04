package org.metplus.cruncher.resume

import org.metplus.cruncher.rating.ProcessCruncher

class ReCrunchAllResumes(
        private val resumeRepository: ResumeRepository,
        private val cruncherProcess: ProcessCruncher<Resume>) {
    fun <T> process(observer: ReCrunchAllResumesObserver<T>): T {
        var numberScheduled = 0
        resumeRepository.getAll().forEach {
            cruncherProcess.addWork(it)
            numberScheduled++
        }
        return observer.onSuccess(numberScheduled)
    }
}

interface ReCrunchAllResumesObserver<T> {
    fun onSuccess(numberScheduled: Int): T
}
