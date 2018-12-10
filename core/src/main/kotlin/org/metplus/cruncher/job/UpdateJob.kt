package org.metplus.cruncher.job

import org.metplus.cruncher.rating.ProcessCruncher

class UpdateJob(
        private val jobsRepository: JobsRepository,
        private val crunchJobProcess: ProcessCruncher<Job>
) {
    fun process(id: String, title: String?, description: String?, observer: UpdateJobObserver) {
        val job = jobsRepository.getById(id) ?: return observer.onNotFound()
        val newTitle = title ?: job.title
        val newDescription = description ?: job.description
        val savedJob = jobsRepository.save(job.copy(title = newTitle, description = newDescription))
        crunchJobProcess.addWork(savedJob)
        observer.onSuccess(savedJob)
    }
}

interface UpdateJobObserver {
    fun onSuccess(job: Job)
    fun onNotFound()
}