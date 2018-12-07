package org.metplus.cruncher.rating


import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.metplus.cruncher.job.Job
import org.metplus.cruncher.job.JobRepositoryFake
import org.metplus.cruncher.job.JobsRepository

/**
 * Created by joao on 3/28/16.
 */
class JobCruncherTests {
    private lateinit var jobRepository: JobsRepository
    private lateinit var jobCruncher: CrunchJobProcess

    private lateinit var cruncherImpl: CruncherStub

    @BeforeEach
    fun setup() {
        cruncherImpl = CruncherStub()
        val allCrunchers = CruncherList(listOf(cruncherImpl))
        jobRepository = JobRepositoryFake()
        jobCruncher = CrunchJobProcess(allCrunchers, jobRepository)
    }

    @Test
    fun `When no jobs need to be processed, starts and stops without calling the cruncher`() {
        jobCruncher.start()
        jobCruncher.stop()
        jobCruncher.join()
    }

    @Test
    fun `When one job need to be processed, it calculates metadata for title and description`() {
        val job = Job("job-id",
                "some title",
                "some description",
                emptyMetaData(),
                emptyMetaData())
        jobRepository.save(job)

        cruncherImpl.crunchReturn = mutableListOf(CruncherMetaData(
                metaData = mutableMapOf("some-key" to 0.1)
        ), CruncherMetaData(
                metaData = mutableMapOf("some-other-key" to 1.0)
        ))

        jobCruncher.start()
        jobCruncher.addWork(job)
        jobCruncher.stop()
        jobCruncher.join()

        assertThat(cruncherImpl.crunchWasCalledWith.size).isEqualTo(2)
        assertThat(cruncherImpl.crunchWasCalledWith).isEqualTo(listOf("some title", "some description"))
        val crunchedJob = jobRepository.getById("job-id")!!
        assertThat(crunchedJob.titleMetaData.metaData["some-key"]).isEqualTo(0.1)
        assertThat(crunchedJob.descriptionMetaData.metaData["some-other-key"]).isEqualTo(1.0)
    }

    @Test
    fun `When two jobs need to be processed, it crunches both`() {
        val job = Job("job-id",
                "some title",
                "some description",
                emptyMetaData(),
                emptyMetaData())
        jobRepository.save(job)
        val secondJob = Job("other-job-id",
                "some other title",
                "some other description",
                emptyMetaData(),
                emptyMetaData())
        jobRepository.save(secondJob)

        cruncherImpl.crunchReturn = mutableListOf(CruncherMetaData(
                metaData = mutableMapOf("some-key" to 0.1)
        ), CruncherMetaData(
                metaData = mutableMapOf("some-other-key" to 1.0)
        ), CruncherMetaData(
                metaData = mutableMapOf("second-some-key" to 99.0)
        ), CruncherMetaData(
                metaData = mutableMapOf("second-some-other-key" to .01)
        ))

        jobCruncher.start()
        jobCruncher.addWork(job)
        jobCruncher.addWork(secondJob)
        jobCruncher.stop()
        jobCruncher.join()

        assertThat(cruncherImpl.crunchWasCalledWith.size).isEqualTo(4)
        assertThat(cruncherImpl.crunchWasCalledWith).isEqualTo(
                listOf(
                        "some title", "some description",
                        "some other title", "some other description"))
        val crunchedJob = jobRepository.getById("job-id")!!
        assertThat(crunchedJob.titleMetaData.metaData["some-key"]).isEqualTo(0.1)
        assertThat(crunchedJob.descriptionMetaData.metaData["some-other-key"]).isEqualTo(1.0)
        val otherCruncherJob = jobRepository.getById("other-job-id")!!
        assertThat(otherCruncherJob.titleMetaData.metaData["second-some-key"]).isEqualTo(99.0)
        assertThat(otherCruncherJob.descriptionMetaData.metaData["second-some-other-key"]).isEqualTo(.01)
    }
}
