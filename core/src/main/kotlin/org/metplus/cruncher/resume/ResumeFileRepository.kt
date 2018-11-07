package org.metplus.cruncher.resume

interface ResumeFileRepository {
    fun save(resumeFile: ResumeFile): ResumeFile
    fun getByUserId(userId: String): ResumeFile?
    fun deleteIfExists(userId: String)
}
