package org.metplus.cruncher.job

class JobRepositoryFakeTest : JobRepositoryTest() {
    private val jobRepository = JobRepoistoryFake()
    override fun getJobRepository(): JobsRepository {
        return jobRepository
    }
}