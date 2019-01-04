package org.metplus.cruncher.resume

class ResumeRepositoryFake: ResumeRepository {

    private val allResumes = mutableMapOf<String, Resume>()

    override fun save(resume: Resume): Resume {
        allResumes[resume.userId] = resume.copy()
        return allResumes[resume.userId]!!
    }

    override fun getByUserId(userId: String): Resume? {
        return allResumes[userId]
    }

    override fun getAll(): List<Resume> {
        return allResumes.values.toList()
    }

    fun removeAll() {
        allResumes.clear()
    }
}