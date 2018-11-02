package org.metplus.cruncher.job

class UpdateJob(
        private val jobsRepository: JobsRepository
) {
    fun process(id: String, title: String?, description: String?, observer: UpdateJobObserver) {
        val job = jobsRepository.getById(id) ?: return observer.onNotFound()
        val newTitle = title ?: job.title
        val newDescription = description ?: job.description
        observer.onSuccess(jobsRepository.save(job.copy(title = newTitle, description = newDescription)))
    }
}

interface UpdateJobObserver {
    fun onSuccess(job: Job)
    fun onNotFound()
}