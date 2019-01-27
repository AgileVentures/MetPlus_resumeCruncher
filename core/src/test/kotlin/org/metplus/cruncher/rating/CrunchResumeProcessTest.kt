package org.metplus.cruncher.rating


import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.metplus.cruncher.resume.FileInputStreamFake
import org.metplus.cruncher.resume.Resume
import org.metplus.cruncher.resume.ResumeFile
import org.metplus.cruncher.resume.ResumeFileRepository
import org.metplus.cruncher.resume.ResumeFileRepositoryFake
import org.metplus.cruncher.resume.ResumeRepository
import org.metplus.cruncher.resume.ResumeRepositoryFake


class CrunchResumeProcessTest {
    private lateinit var cruncher: CrunchResumeProcess
    private lateinit var cruncherImpl: CruncherStub
    private lateinit var resumeRepository: ResumeRepository
    private lateinit var resumeFileRepository: ResumeFileRepository

    @BeforeEach
    fun setup() {
        cruncherImpl = CruncherStub()
        val allCrunchers = CruncherList(listOf(cruncherImpl))
        resumeRepository = ResumeRepositoryFake()
        resumeFileRepository = ResumeFileRepositoryFake()
        cruncher = CrunchResumeProcess(
                allCrunchers = allCrunchers,
                resumeRepository = resumeRepository,
                resumeFileRepository = resumeFileRepository
        )
    }

    @Test
    fun `When no resumes need to be processed, starts and stops without calling the cruncher`() {
        cruncher.start()
        cruncher.stop()
        cruncher.join()
    }

    @Test
    fun `When one resume need to be processed, it gets crunched`() {
        val resume = resumeRepository.save(
                Resume(
                        filename = "some-file-name.pdf",
                        userId = "some-user-id",
                        fileType = "pdf",
                        cruncherData = mutableMapOf())
        )
        resumeFileRepository.save(ResumeFile(
                filename = "some-file-name.pdf",
                userId = "some-user-id",
                fileStream = FileInputStreamFake("Some text in the file")
        ))
        cruncherImpl.crunchReturn = mutableListOf(CruncherMetaData(
                metaData = mutableMapOf("some-key" to 10.0)
        ))

        cruncher.start()
        cruncher.addWork(resume)
        cruncher.stop()
        cruncher.join()

        assertThat(cruncherImpl.crunchWasCalledWith.size).isEqualTo(1)
        assertThat(cruncherImpl.crunchWasCalledWith.first()).contains("Some text in the file")
        val cruncherResume = resumeRepository.getByUserId("some-user-id")!!
        assertThat(cruncherResume.cruncherData["some-cruncher"]!!.metaData["some-key"]).isEqualTo(10.0)
    }

    @Test
    fun `when 2 resumes are added, it process both of them`() {
        val resumeUser1 = resumeRepository.save(
                Resume(
                        filename = "some-file-name.pdf",
                        userId = "some-user-id",
                        fileType = "pdf",
                        cruncherData = mutableMapOf())
        )
        resumeFileRepository.save(ResumeFile(
                filename = "some-file-name.pdf",
                userId = "some-user-id",
                fileStream = FileInputStreamFake("Some text in the file")
        ))
        cruncherImpl.crunchReturn.add(CruncherMetaData(
                metaData = mutableMapOf("some-key" to 10.0)))

        val resumeUser2 = resumeRepository.save(
                Resume(
                        filename = "some-other-file-name.pdf",
                        userId = "some-other-user-id",
                        fileType = "pdf",
                        cruncherData = mutableMapOf("some-cruncher" to CruncherMetaData(mutableMapOf("the-key" to 99.0))))
        )

        resumeFileRepository.save(ResumeFile(
                filename = "some-file-name.pdf",
                userId = "some-other-user-id",
                fileStream = FileInputStreamFake("Some other text in the file")
        ))
        cruncherImpl.crunchReturn.add(CruncherMetaData(
                        metaData = mutableMapOf("some-other-key" to 9.0)))

        cruncher.start()
        cruncher.addWork(resumeUser1)
        cruncher.addWork(resumeUser2)
        cruncher.stop()
        cruncher.join()
        assertThat(cruncherImpl.crunchWasCalledWith.size).isEqualTo(2)
        assertThat(cruncherImpl.crunchWasCalledWith.first()).contains("Some text in the file")
        assertThat(cruncherImpl.crunchWasCalledWith[1]).contains("Some other text in the file")

        val cruncherResumeUser1 = resumeRepository.getByUserId("some-user-id")!!
        assertThat(cruncherResumeUser1.cruncherData["some-cruncher"]!!.metaData["some-key"]).isEqualTo(10.0)

        val cruncherResumeUser2 = resumeRepository.getByUserId("some-other-user-id")!!
        assertThat(cruncherResumeUser2.cruncherData["some-cruncher"]!!.metaData["some-other-key"]).isEqualTo(9.0)
    }

    @Test
    fun `When resume need to be processed but cannot find file, starts and stops without calling the cruncher`() {
        val resumeUser1 = resumeRepository.save(
                Resume(
                        filename = "some-file-name.pdf",
                        userId = "some-user-id",
                        fileType = "pdf",
                        cruncherData = mapOf())
        )
        cruncher.start()
        cruncher.addWork(resumeUser1)
        cruncher.stop()
        cruncher.join()
    }
}
