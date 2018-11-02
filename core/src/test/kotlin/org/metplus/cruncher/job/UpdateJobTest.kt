package org.metplus.cruncher.job

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class UpdateJobTest {
    var jobsRepository: JobRepositoryFake = JobRepositoryFake()
    @BeforeEach
    fun setup() {
        jobsRepository.removeAll()
    }

    @Test
    fun `when job being updated cannot be found, it calls the onNotFound observer`() {
        val updateJob = UpdateJob(jobsRepository)

        var wasOnNotFoundCalled = false
        updateJob.process("1", "Some title", "Some descriptions", object : UpdateJobObserver {
            override fun onSuccess(job: Job) {
                fail("Should not have called onSuccess")
            }

            override fun onNotFound() {
                wasOnNotFoundCalled = true
            }
        })
        assertThat(wasOnNotFoundCalled).isTrue()
    }

    @Test
    fun `when job being updated can be found, it calls the onSuccess observer and update the job`() {
        jobsRepository.save(Job("1", "Some title", "Some description"))
        val updateJob = UpdateJob(jobsRepository)

        var wasOnSuccessCalled = false
        updateJob.process("1", "Some other title", "Some other description", object : UpdateJobObserver {
            override fun onSuccess(job: Job) {
                wasOnSuccessCalled = true
                assertThat(job.id).isEqualTo("1")
                assertThat(job.title).isEqualTo("Some other title")
                assertThat(job.description).isEqualTo("Some other description")
            }

            override fun onNotFound() {
                fail("Should not have called onNotFound")
            }
        })
        assertThat(wasOnSuccessCalled).isTrue()
    }

    @Test
    fun `when job being updated can be found and only title is present, it calls the onSuccess observer and only update the title job`() {
        jobsRepository.save(Job("1", "Some title", "Some description"))
        val updateJob = UpdateJob(jobsRepository)

        var wasOnSuccessCalled = false
        updateJob.process("1", "Some other title", null, object : UpdateJobObserver {
            override fun onSuccess(job: Job) {
                wasOnSuccessCalled = true
                assertThat(job.id).isEqualTo("1")
                assertThat(job.title).isEqualTo("Some other title")
                assertThat(job.description).isEqualTo("Some description")
            }

            override fun onNotFound() {
                fail("Should not have called onNotFound")
            }
        })
        assertThat(wasOnSuccessCalled).isTrue()
    }

    @Test
    fun `when job being updated can be found and only description is present, it calls the onSuccess observer and only update the description job`() {
        jobsRepository.save(Job("1", "Some title", "Some description"))
        val updateJob = UpdateJob(jobsRepository)

        var wasOnSuccessCalled = false
        updateJob.process("1", null, "Some other description", object : UpdateJobObserver {
            override fun onSuccess(job: Job) {
                wasOnSuccessCalled = true
                assertThat(job.id).isEqualTo("1")
                assertThat(job.title).isEqualTo("Some title")
                assertThat(job.description).isEqualTo("Some other description")
            }

            override fun onNotFound() {
                fail("Should not have called onNotFound")
            }
        })
        assertThat(wasOnSuccessCalled).isTrue()
    }
}