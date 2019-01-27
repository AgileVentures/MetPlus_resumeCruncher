package org.metplus.cruncher.resume

import java.lang.Exception

class ResumeRepositoryStub : ResumeRepository {
    var throwOnSave: Exception? = null

    override fun save(resume: Resume): Resume {
        if (throwOnSave != null)
            throw(throwOnSave!!)

        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getByUserId(userId: String): Resume? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getAll(): List<Resume> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}