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

    override fun getAll(): List<Resume> {
        return resumeRepositoryMongo.findAll().map { it.toResume() }
    }
}

private fun ResumeMongo.toResume(): Resume {
    val data = mutableMapOf<String, CruncherMetaData>()
    cruncherData?.forEach {
        val cruncherName = it.key
        data[cruncherName] = CruncherMetaData(mutableMapOf())

        it.value?.dataFields?.forEach {
            data[cruncherName]?.metaData?.set(it.key, it.value.data as Double)
        }
    }

    return Resume(
            userId = id,
            fileType = fileType,
            filename = filename,
            cruncherData = data
    )
}

private fun Resume.toResumeMongo(): ResumeMongo {
    val metaData = mutableMapOf<String, MetaData>()
    cruncherData.forEach {
        val cruncherName = it.key
        val data = mutableMapOf<String, MetaDataField<*>>()
        it.value.metaData.forEach {
            data[it.key] = MetaDataField(it.value)
        }
        metaData[cruncherName] = MetaData(data as HashMap<String, MetaDataField<*>>)
    }
    return ResumeMongo(
            id = userId,
            filename = filename,
            fileType = fileType,
            cruncherData = metaData
    )
}
