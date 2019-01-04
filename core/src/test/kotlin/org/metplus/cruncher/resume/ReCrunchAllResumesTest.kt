package org.metplus.cruncher.resume

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.metplus.cruncher.rating.CrunchResumeProcessSpy
import org.metplus.cruncher.rating.emptyMetaData

internal class ReCrunchAllResumesTest {
    @Test
    fun `when invoked schedule all resumes to be crunched`() {
        val resumeRepository = ResumeRepositoryFake()
        val cruncherProcess = CrunchResumeProcessSpy()

        resumeRepository.save(Resume("filename", "user-id", "pdf", emptyMetaData()))
        resumeRepository.save(Resume("filename", "other-user-id", "pdf", emptyMetaData()))
        resumeRepository.save(Resume("filename", "yet-other-user-id", "pdf", emptyMetaData()))

        val subject = ReCrunchAllResumes(resumeRepository, cruncherProcess)
        var numberJobsScheduled: Int = 100000
        subject.process(object : ReCrunchAllResumesObserver<Boolean> {
            override fun onSuccess(numberScheduled: Int): Boolean {
                numberJobsScheduled = numberScheduled
                return true
            }
        })

        Assertions.assertThat(numberJobsScheduled).isEqualTo(3)
    }
}