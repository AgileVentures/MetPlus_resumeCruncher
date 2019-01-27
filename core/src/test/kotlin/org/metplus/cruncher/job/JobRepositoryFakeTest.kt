package org.metplus.cruncher.job

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class JobRepositoryFakeTest : JobRepositoryTest() {
    private val jobRepository = JobRepositoryFake()
    override fun getJobRepository(): JobsRepository {
        return jobRepository
    }

    @Test
    fun `when removeAll is called, all jobs are removed`() {
        jobRepository.save(Job("1", "Some title", "Some description", mapOf(), mapOf()))
        jobRepository.save(Job("2", "Some other title", "Some other description", mapOf(), mapOf()))
        jobRepository.save(Job("3", "Yet another title", "Yet another description", mapOf(), mapOf()))
        assertThat(jobRepository.getById("1")).isNotNull
        assertThat(jobRepository.getById("2")).isNotNull
        assertThat(jobRepository.getById("3")).isNotNull

        jobRepository.removeAll()

        assertThat(jobRepository.getById("1")).isNull()
        assertThat(jobRepository.getById("2")).isNull()
        assertThat(jobRepository.getById("3")).isNull()
    }
}