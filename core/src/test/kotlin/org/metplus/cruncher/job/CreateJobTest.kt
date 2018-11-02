package org.metplus.cruncher.job

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

internal class CreateJobTest {
    private lateinit var jobsRepository: JobsRepository
    private lateinit var createJob: CreateJob
    @BeforeEach
    fun setup() {
        jobsRepository = JobRepositoryFake()
        createJob = CreateJob(jobsRepository)
    }

    @Test
    fun `when job does not exist, it creates the job`() {
        val newJob = Job(
                "someid",
                "some title",
                "some description")
        var wasObserverCalled = false

        createJob.process(newJob, object : CreateJobObserver {
            override fun onSuccess(job: Job) {
                    assertThat(job).isEqualToComparingFieldByField(newJob)
                    wasObserverCalled = true
            }

            override fun onAlreadyExists() {
                fail("Should not have called onAlreadyExists observer")
            }

        })

        assertThat(wasObserverCalled).isTrue()
        assertThat(jobsRepository.getById("someid")).isEqualToComparingFieldByField(newJob)
    }

    @Test
    fun `when job exists, it calls the correct observer`() {
        val newJob = Job(
                "someid",
                "some title",
                "some description")
        var wasObserverCalled = false
        jobsRepository.save(newJob)

        createJob.process(newJob, object : CreateJobObserver {
            override fun onSuccess(job: Job) {
                fail("Should not have called onSuccess observer")
            }

            override fun onAlreadyExists() {
                wasObserverCalled = true
            }

        })

        assertThat(wasObserverCalled).isTrue()
        assertThat(jobsRepository.getById("someid")).isEqualToComparingFieldByField(newJob)
    }
}
