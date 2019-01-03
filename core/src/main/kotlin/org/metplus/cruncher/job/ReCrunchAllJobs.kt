package org.metplus.cruncher.job

import org.metplus.cruncher.rating.ProcessCruncher

class ReCrunchAllJobs(
        private val jobsRepository: JobsRepository,
        private val jobProcess: ProcessCruncher<Job>
) {
    fun <T> process(observer: ReCrunchAllJobsObserver<T>): T {
        val allJobs = jobsRepository.getAll()

        allJobs.forEach {
            jobProcess.addWork(it)
        }

        return observer.onSuccess(allJobs.size)
    }
}

interface ReCrunchAllJobsObserver<T> {
    fun onSuccess(numberScheduled: Int): T
}
