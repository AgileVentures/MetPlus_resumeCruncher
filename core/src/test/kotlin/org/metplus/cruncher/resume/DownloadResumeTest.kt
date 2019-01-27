package org.metplus.cruncher.resume

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.metplus.cruncher.rating.CruncherMetaData

internal class DownloadResumeTest {
    private lateinit var resumeRepository: ResumeRepository
    private lateinit var downloadResume: DownloadResume
    private lateinit var resumeFileRepository: ResumeFileRepository

    @BeforeEach
    fun setup() {
        resumeRepository = ResumeRepositoryFake()
        resumeFileRepository = ResumeFileRepositoryFake()
        downloadResume = DownloadResume(resumeRepository, resumeFileRepository)
    }

    @Test
    fun `when retrieving a resume that does not exist, it call the onResumeNotFound observer`() {
        assertThat(downloadResume.process("someUserId", object : DownloadResumeObserver<Boolean> {
            override fun onError(userId: String, exception: Exception): Boolean {
                fail("Called onError when onResumeNotFound was expected")
            }

            override fun onSuccess(resume: Resume, resumeFile: ResumeFile): Boolean {
                fail("Called onSuccess when onResumeNotFound was expected")
            }

            override fun onResumeNotFound(userId: String): Boolean {
                return true
            }
        })).isTrue()
    }

    @Test
    fun `when retrieving a resume that exist, it call the onSuccess observer with the resume and resume file`() {
        val resumeSaved = Resume(
                filename = "some_file_name.pdf",
                userId = "someUserId",
                fileType = "pdf",
                cruncherData = mapOf()
        )
        val resumeFileSaved = ResumeFile(
                filename = "some_file_name.pdf",
                userId = "someUserId",
                fileStream = FileInputStreamFake("some content")
        )
        resumeRepository.save(resumeSaved)
        resumeFileRepository.save(resumeFileSaved)

        assertThat(downloadResume.process("someUserId", object : DownloadResumeObserver<Boolean> {
            override fun onError(userId: String, exception: Exception): Boolean {
                fail("Called onError when onSuccess was expected")
            }

            override fun onSuccess(resume: Resume, resumeFile: ResumeFile): Boolean {
                assertThat(resume).isEqualToComparingFieldByField(resumeSaved)
                assertThat(resumeFile).isEqualToComparingFieldByField(resumeFileSaved)
                return true
            }

            override fun onResumeNotFound(userId: String): Boolean {
                fail("Called onResumeNotFound when onSuccess was expected")
            }
        })).isTrue()
    }

    @Test
    fun `when cannot retrieve the file, it call the onError observer`() {
        val resumeSaved = Resume(
                filename = "some_file_name.pdf",
                userId = "someUserId",
                fileType = "pdf",
                cruncherData = mapOf()
        )
        resumeRepository.save(resumeSaved)

        assertThat(downloadResume.process("someUserId", object : DownloadResumeObserver<Boolean> {
            override fun onError(userId: String, exception: Exception): Boolean {
                assertThat(exception.message).isEqualTo("Resume for user 'someUserId' not found")
                return true
            }

            override fun onSuccess(resume: Resume, resumeFile: ResumeFile): Boolean {
                fail("Called onSuccess when onError was expected")
            }

            override fun onResumeNotFound(userId: String): Boolean {
                fail("Called onResumeNotFound when onError was expected")
            }
        })).isTrue()
    }
}
