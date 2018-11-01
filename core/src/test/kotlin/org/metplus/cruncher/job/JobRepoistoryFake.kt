package org.metplus.cruncher.job

class JobRepoistoryFake: JobsRepository {

    val jobs = mutableMapOf<String, Job>()

    override fun save(job: Job): Job {
        jobs[job.id] = job.copy()
        return jobs[job.id]!!
    }

    override fun getById(id: String): Job? {
        return jobs[id]
    }
}