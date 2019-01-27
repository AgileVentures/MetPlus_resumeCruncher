package org.metplus.cruncher.persistence.model

import org.metplus.cruncher.job.Job
import org.metplus.cruncher.job.JobsRepository
import org.metplus.cruncher.rating.CruncherMetaData

class JobRepositoryImpl(
        private val jobRepository: JobRepositoryMongo
) : JobsRepository {
    override fun save(job: Job): Job {
        return jobRepository.save(job.toJobMongo()).toJob()
    }

    override fun getById(id: String): Job? {
        return jobRepository.getById(id)?.toJob()
    }

    override fun getAll(): List<Job> {
        return jobRepository.findAll().map { it.toJob() }
    }
}

private fun JobMongo.toJob(): Job {
    val titledata = titleMetadata ?: mutableMapOf()
    val descdata = titleMetadata ?: mutableMapOf()
    return Job(
            id = id,
            title = title,
            titleMetaData = titledata,
            description = description,
            descriptionMetaData = descdata
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
