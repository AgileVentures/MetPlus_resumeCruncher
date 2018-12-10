package org.metplus.cruncher.job

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.metplus.cruncher.rating.CrunchJobProcessSpy
import org.metplus.cruncher.rating.emptyMetaData

internal class UpdateJobTest {
    lateinit var jobsRepository: JobRepositoryFake
    lateinit var crunchJobProcess: CrunchJobProcessSpy
    lateinit var updateJob: UpdateJob
    @BeforeEach
    fun setup() {
        jobsRepository = JobRepositoryFake()
        crunchJobProcess = CrunchJobProcessSpy()
        updateJob = UpdateJob(jobsRepository, crunchJobProcess)
    }

    @Test
    fun `when job being updated cannot be found, it calls the onNotFound observer and no job is marked to be crunched`() {
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
        assertThat(crunchJobProcess.nextWorkInQueue()).isNull()
    }

    @Test
    fun `when job being updated can be found, it calls the onSuccess observer and update the job and job is marked to be crunched`() {
        jobsRepository.save(Job("1", "Some title", "Some description", emptyMetaData(), emptyMetaData()))

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
        assertThat(crunchJobProcess.nextWorkInQueue()).isEqualTo(Job("1", "Some other title", "Some other description", emptyMetaData(), emptyMetaData()))
    }

    @Test
    fun `when job being updated can be found and only title is present, it calls the onSuccess observer and only update the title job and job is marked to be crunched`() {
        jobsRepository.save(Job("1", "Some title", "Some description", emptyMetaData(), emptyMetaData()))

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
        assertThat(crunchJobProcess.nextWorkInQueue()).isEqualTo(Job("1", "Some other title", "Some description", emptyMetaData(), emptyMetaData()))
    }

    @Test
    fun `when job being updated can be found and only description is present, it calls the onSuccess observer and only update the description job and job is marked to be crunched`() {
        jobsRepository.save(Job("1", "Some title", "Some description", emptyMetaData(), emptyMetaData()))

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
        assertThat(crunchJobProcess.nextWorkInQueue()).isEqualTo(Job("1", "Some title", "Some other description", emptyMetaData(), emptyMetaData()))
    }
}