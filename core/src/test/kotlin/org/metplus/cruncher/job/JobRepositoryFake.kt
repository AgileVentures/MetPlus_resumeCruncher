package org.metplus.cruncher.job

class JobRepositoryFake : JobsRepository {

    private val jobs = mutableMapOf<String, Job>()

    override fun save(job: Job): Job {
        jobs[job.id] = job.copy()
        return jobs[job.id]!!
    }

    override fun getById(id: String): Job? {
        return jobs[id]
    }

    override fun getAll(): List<Job> {
        return jobs.values.toList()
    }

    fun removeAll() {
        jobs.clear()
    }
}