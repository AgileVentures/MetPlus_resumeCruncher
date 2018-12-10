package org.metplus.cruncher.job

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.metplus.cruncher.rating.CrunchJobProcessSpy
import org.metplus.cruncher.rating.emptyMetaData

internal class CreateJobTest {
    private lateinit var jobsRepository: JobsRepository
    private lateinit var createJob: CreateJob
    private lateinit var processCruncher: CrunchJobProcessSpy
    @BeforeEach
    fun setup() {
        jobsRepository = JobRepositoryFake()
        processCruncher = CrunchJobProcessSpy()
        createJob = CreateJob(jobsRepository, processCruncher)
    }

    @Test
    fun `when job does not exist, it creates the job and marks the job to be crunched`() {
        val newJob = Job(
                "someid",
                "some title",
                "some description",
                emptyMetaData(),
                emptyMetaData())
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
        assertThat(processCruncher.nextWorkInQueue()).isEqualTo(newJob)
    }

    @Test
    fun `when job exists, it calls the correct observer and does not marks the job to be crunched`() {
        val existingJob = Job(
                "someid",
                "some title",
                "some description",
                emptyMetaData(),
                emptyMetaData())
        var wasObserverCalled = false
        jobsRepository.save(existingJob)

        createJob.process(existingJob, object : CreateJobObserver {
            override fun onSuccess(job: Job) {
                fail("Should not have called onSuccess observer")
            }

            override fun onAlreadyExists() {
                wasObserverCalled = true
            }

        })

        assertThat(wasObserverCalled).isTrue()
        assertThat(jobsRepository.getById("someid")).isEqualToComparingFieldByField(existingJob)
        assertThat(processCruncher.nextWorkInQueue()).isEqualTo(null)
    }
}
