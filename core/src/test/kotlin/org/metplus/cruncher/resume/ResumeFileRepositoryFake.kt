package org.metplus.cruncher.resume


class ResumeFileRepositoryFake : ResumeFileRepository {
    override fun deleteIfExists(userId: String) {
        allResumeFiles.remove(userId)
    }

    private val allResumeFiles = mutableMapOf<String, ResumeFile>()

    override fun save(resumeFile: ResumeFile): ResumeFile {
        allResumeFiles[resumeFile.userId] = resumeFile.copy()
        return allResumeFiles[resumeFile.userId]!!
    }

    override fun getByUserId(userId: String): ResumeFile {
        return allResumeFiles[userId] ?: throw ResumeNotFound("Resume for user '$userId' not found")
    }
}