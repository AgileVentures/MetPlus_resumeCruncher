package org.metplus.cruncher.job

class CreateJob(
        private val jobsRepository: JobsRepository
) {
    fun process(job: Job, observer: CreateJobObserver) {
        if (jobsRepository.getById(job.id) != null)
            return observer.onAlreadyExists()
        return observer.onSuccess(jobsRepository.save(job))
    }
}

interface CreateJobObserver {
    fun onSuccess(job: Job)
    fun onAlreadyExists()
}