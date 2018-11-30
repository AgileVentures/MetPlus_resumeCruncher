package org.metplus.cruncher.persistence.model

import org.metplus.cruncher.rating.CruncherMetaData
import org.metplus.cruncher.resume.Resume
import org.metplus.cruncher.resume.ResumeRepository

class ResumeRepositoryImpl(
        private val resumeRepositoryMongo: ResumeRepositoryMongo
) : ResumeRepository {
    override fun save(resume: Resume): Resume {
        return resumeRepositoryMongo.save(resume.toResumeMongo()).toResume()
    }

    override fun getByUserId(userId: String): Resume? {
        return resumeRepositoryMongo.getById(userId)?.toResume()
    }
}

private fun ResumeMongo.toResume(): Resume {
    val data = mutableMapOf<String, Double>()
    cruncherData.dataFields.forEach {
        data[it.key] = it.value.data as Double
    }

    return Resume(
            userId = id,
            fileType = fileType,
            filename = filename,
            cruncherData = CruncherMetaData(data)
    )
}

private fun Resume.toResumeMongo(): ResumeMongo {
    val data = mutableMapOf<String, MetaDataField<*>>()
    cruncherData.metaData.forEach {
        data[it.key] = MetaDataField(it.value)
    }
    return ResumeMongo(
            id = userId,
            filename = filename,
            fileType = fileType,
            cruncherData = MetaData(data as HashMap<String, MetaDataField<*>>)
    )
}
