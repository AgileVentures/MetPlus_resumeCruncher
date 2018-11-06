package org.metplus.cruncher.persistence.model

import org.metplus.cruncher.resume.Resume
import org.metplus.cruncher.resume.ResumeRepository

class ResumeRepositoryImpl(
        private val resumeRepositoryMongo: ResumeRepositoryMongo
): ResumeRepository {
    override fun save(resume: Resume): Resume {
        return resumeRepositoryMongo.save(resume.toResumeMongo()).toResume()
    }

    override fun getByUserId(userId: String): Resume? {
        return resumeRepositoryMongo.getById(userId)?.toResume()
    }
}

private fun ResumeMongo.toResume(): Resume {
    return Resume(
            userId = id,
            fileType = fileType,
            filename = filename
    )
}

private fun Resume.toResumeMongo(): ResumeMongo {
    return ResumeMongo(
            id = userId,
            filename = filename,
            fileType = fileType
    )
}
