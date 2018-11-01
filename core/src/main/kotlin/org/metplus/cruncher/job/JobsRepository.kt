package org.metplus.cruncher.job

interface JobsRepository {
    fun save(job: Job): Job
    fun getById(id: String): Job?
}