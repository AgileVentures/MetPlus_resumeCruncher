package org.metplus.cruncher.resume

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.metplus.cruncher.rating.CrunchResumeProcessSpy
import org.metplus.cruncher.rating.CruncherMetaData

internal class UploadResumeTest {
    private lateinit var resumeRepository: ResumeRepository
    private lateinit var uploadResume: UploadResume
    private lateinit var resumeFileRepository: ResumeFileRepository
    private lateinit var cruncherResumeProcessSpy: CrunchResumeProcessSpy

    @BeforeEach
    fun setup() {
        resumeRepository = ResumeRepositoryFake()
        resumeFileRepository = ResumeFileRepositoryFake()
        cruncherResumeProcessSpy = CrunchResumeProcessSpy()
        uploadResume = UploadResume(resumeRepository, resumeFileRepository, cruncherResumeProcessSpy)
    }

    @Test
    fun `when resume does not exist, it creates a new resume and enqueues the resume to be processed`() {
        val newResume = Resume(
                userId = "someUserId",
                fileType = "pdf",
                filename = "some_file_name.pdf",
                cruncherData = mapOf()
        )
        val fileInputStream = FileInputStreamFake("some content")
        assertThat(uploadResume.process(userId = "someUserId",
                resumeName = "some_file_name.pdf",
                file = fileInputStream,
                size = 1,
                observer = object : UploadResumeObserver<Boolean> {
                    override fun onException(exception: Exception, resume: Resume): Boolean {
                        fail("Called onException when onSuccess was expected")
                        return false
                    }

                    override fun onSuccess(resume: Resume): Boolean {
                        return true
                    }

                    override fun onEmptyFile(resume: Resume): Boolean {
                        fail("Called onEmptyFile when onSuccess was expected")
                        return false
                    }

                }) as Boolean).isTrue()

        assertThat(resumeRepository.getByUserId("someUserId"))
                .isEqualToComparingFieldByField(newResume)
        assertThat(resumeFileRepository.getByUserId("someUserId"))
                .isNotNull
        assertThat(cruncherResumeProcessSpy.nextWorkInQueue()).isEqualTo(newResume)
    }

    @Test
    fun `when resume exists, it overrides with new resume and saves the file and enqueues the resume to be processed`() {
        resumeRepository.save(Resume(
                filename = "some_file_name.pdf",
                fileType = "pdf",
                userId = "someUserId",
                cruncherData = mapOf("cruncher1" to CruncherMetaData(metaData = hashMapOf()))
        ))

        val newResume = Resume(
                userId = "someUserId",
                fileType = "doc",
                filename = "some_other_file.doc",
                cruncherData = mapOf()
        )

        val fileInputStream = FileInputStreamFake("some content")
        assertThat(uploadResume.process(userId = "someUserId",
                resumeName = "some_other_file.doc",
                file = fileInputStream,
                size = 1,
                observer = object : UploadResumeObserver<Boolean> {
                    override fun onException(exception: Exception, resume: Resume): Boolean {
                        fail("Called onException when onSuccess was expected")
                        return false
                    }

                    override fun onSuccess(resume: Resume): Boolean {
                        return true
                    }

                    override fun onEmptyFile(resume: Resume): Boolean {
                        fail("Called onEmptyFile when onSuccess was expected")
                        return false
                    }

                }) as Boolean).isTrue()

        assertThat(resumeRepository.getByUserId("someUserId"))
                .isEqualToComparingFieldByField(newResume)
        assertThat(cruncherResumeProcessSpy.nextWorkInQueue()).isEqualTo(newResume)
    }

    @Test
    fun `when resume file is empty, it does not save the file and call the onEmptyFile observer`() {
        val fileInputStream = FileInputStreamFake("some content")
        assertThat(uploadResume.process(userId = "someUserId",
                resumeName = "some_other_file.doc",
                file = fileInputStream,
                size = 0,
                observer = object : UploadResumeObserver<Boolean> {
                    override fun onException(exception: Exception, resume: Resume): Boolean {
                        fail("Called onException when onEmptyFile was expected")
                        return false
                    }

                    override fun onSuccess(resume: Resume): Boolean {
                        fail("Called onSuccess when onSuccess was expected")
                        return false
                    }

                    override fun onEmptyFile(resume: Resume): Boolean {
                        return true
                    }

                }) as Boolean).isTrue()

        assertThat(resumeRepository.getByUserId("someUserId")).isNull()
        assertThat(cruncherResumeProcessSpy.nextWorkInQueue()).isNull()
    }

    @Test
    fun `when resume throw exception while saving, it does not save the file and call the onException observer`() {
        resumeRepository = ResumeRepositoryStub()
        (resumeRepository as ResumeRepositoryStub).throwOnSave = Exception("Some exception")
        uploadResume = UploadResume(resumeRepository, resumeFileRepository, cruncherResumeProcessSpy)

        val fileInputStream = FileInputStreamFake("some content")
        assertThat(uploadResume.process(userId = "someUserId",
                resumeName = "some_other_file.doc",
                file = fileInputStream,
                size = 1,
                observer = object : UploadResumeObserver<Boolean> {
                    override fun onException(exception: Exception, resume: Resume): Boolean {
                        assertThat(exception.message).isEqualTo("Some exception")
                        return true
                    }

                    override fun onSuccess(resume: Resume): Boolean {
                        fail("Called onSuccess when onSuccess was expected")
                        return false
                    }

                    override fun onEmptyFile(resume: Resume): Boolean {
                        fail("Called onException when onEmptyFile was expected")
                        return false
                    }

                }) as Boolean).isTrue()
        try {
            resumeFileRepository.getByUserId("someUserId")
            fail("Exception should have been thrown")
        } catch (exception: ResumeNotFound) {
            assertThat(cruncherResumeProcessSpy.nextWorkInQueue()).isNull()
        }
    }
}
