package org.metplus.cruncher.job

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.metplus.cruncher.rating.CrunchJobProcessSpy
import org.metplus.cruncher.rating.emptyMetaData

internal class ReCrunchAllJobsTest {
    @Test
    fun `when invoked schedule all jobs to be crunched`() {
        val jobsRepository = JobRepositoryFake()
        val cruncherProcess = CrunchJobProcessSpy()

        jobsRepository.save(Job("job1", "some title", "some description", emptyMetaData(), emptyMetaData()))
        jobsRepository.save(Job("job2", "some other title", "some other description", emptyMetaData(), emptyMetaData()))
        jobsRepository.save(Job("job3", "yet some other title", "yet some other description", emptyMetaData(), emptyMetaData()))

        val subject = ReCrunchAllJobs(jobsRepository, cruncherProcess)
        var numberJobsScheduled: Int = 100000
        subject.process(object : ReCrunchAllJobsObserver<Boolean> {
            override fun onSuccess(numberScheduled: Int): Boolean {
                numberJobsScheduled = numberScheduled
                return true
            }
        })

        assertThat(numberJobsScheduled).isEqualTo(3)
    }
}