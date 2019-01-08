package org.metplus.cruncher.job

import org.metplus.cruncher.rating.ProcessCruncher
import org.slf4j.LoggerFactory

class CreateJob(
        private val jobsRepository: JobsRepository,
        private val crunchJobProcess: ProcessCruncher<Job>
) {
    fun process(job: Job, observer: CreateJobObserver) {
        logger.trace("CreateJob($job)")
        if (jobsRepository.getById(job.id) != null) {
            logger.warn("Unable to find job with id: `${job.id}`")
            return observer.onAlreadyExists()
        }

        val savedJob = jobsRepository.save(job)
        crunchJobProcess.addWork(savedJob)
        logger.debug("Created the job with id: ${job.id} with success")
        return observer.onSuccess(savedJob)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(CreateJob::class.java)
    }
}

interface CreateJobObserver {
    fun onSuccess(job: Job)
    fun onAlreadyExists()
}