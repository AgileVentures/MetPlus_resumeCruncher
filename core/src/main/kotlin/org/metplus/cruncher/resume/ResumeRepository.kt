package org.metplus.cruncher.resume

interface ResumeRepository {
    fun save(resume: Resume): Resume
    fun getByUserId(userId: String): Resume?
    fun getAll(): List<Resume>
}
