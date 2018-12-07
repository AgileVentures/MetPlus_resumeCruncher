package org.metplus.cruncher.persistence.model

import org.metplus.cruncher.job.Job
import org.metplus.cruncher.job.JobsRepository

class JobRepositoryImpl(
        private val jobRepository: JobRepositoryMongo
) : JobsRepository {
    override fun save(job: Job): Job {
        return jobRepository.save(job.toJobMongo()).toJob()
    }

    override fun getById(id: String): Job? {
        return jobRepository.getById(id)?.toJob()
    }
}

private fun JobMongo.toJob(): Job {
    return Job(
            id = id,
            title = title,
            titleMetaData = titleMetadata,
            description = description,
            descriptionMetaData = descriptionMetadata
    )
}

private fun Job.toJobMongo(): JobMongo {
    return JobMongo(
            id = id,
            title = title,
            titleMetadata = titleMetaData,
            description = description,
            descriptionMetadata = descriptionMetaData
    )
}
