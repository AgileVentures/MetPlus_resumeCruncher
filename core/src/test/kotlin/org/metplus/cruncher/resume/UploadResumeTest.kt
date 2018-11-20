package org.metplus.cruncher.resume

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.metplus.cruncher.rating.CruncherMetaData

internal class UploadResumeTest {
    private lateinit var resumeRepository: ResumeRepository
    private lateinit var uploadResume: UploadResume
    private lateinit var resumeFileRepository: ResumeFileRepository

    @BeforeEach
    fun setup() {
        resumeRepository = ResumeRepositoryFake()
        resumeFileRepository = ResumeFileRepositoryFake()
        uploadResume = UploadResume(resumeRepository, resumeFileRepository)
    }

    @Test
    fun `when resume does not exist, it creates a new resume`() {
        val newResume = Resume(
                userId = "someUserId",
                fileType = "pdf",
                filename = "some_file_name.pdf",
                cruncherData = CruncherMetaData(metaData = hashMapOf())
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
    }

    @Test
    fun `when resume exists, it overrides with new resume and saves the file`() {
        resumeRepository.save(Resume(
                filename = "some_file_name.pdf",
                fileType = "pdf",
                userId = "someUserId",
                cruncherData = CruncherMetaData(metaData = hashMapOf())
        ))

        val newResume = Resume(
                userId = "someUserId",
                fileType = "doc",
                filename = "some_other_file.doc",
                cruncherData = CruncherMetaData(metaData = hashMapOf())
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
    }

    @Test
    fun `when resume throw exception while saving, it does not save the file and call the onException observer`() {
        resumeRepository = ResumeRepositoryStub()
        (resumeRepository as ResumeRepositoryStub).throwOnSave = Exception("Some exception")
        uploadResume = UploadResume(resumeRepository, resumeFileRepository)

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
        try{
            resumeFileRepository.getByUserId("someUserId")
            fail("Exception should have been thrown")
        } catch (exception: ResumeNotFound) {}
    }
}
