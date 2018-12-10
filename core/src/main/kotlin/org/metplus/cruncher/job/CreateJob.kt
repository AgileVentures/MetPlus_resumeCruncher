package org.metplus.cruncher.job

import org.metplus.cruncher.rating.ProcessCruncher

class CreateJob(
        private val jobsRepository: JobsRepository,
        private val crunchJobProcess: ProcessCruncher<Job>
) {
    fun process(job: Job, observer: CreateJobObserver) {
        if (jobsRepository.getById(job.id) != null)
            return observer.onAlreadyExists()

        val savedJob = jobsRepository.save(job)
        crunchJobProcess.addWork(savedJob)
        return observer.onSuccess(savedJob)
    }
}

interface CreateJobObserver {
    fun onSuccess(job: Job)
    fun onAlreadyExists()
}