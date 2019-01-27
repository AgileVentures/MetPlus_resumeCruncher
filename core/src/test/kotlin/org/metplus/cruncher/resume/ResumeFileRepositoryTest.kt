package org.metplus.cruncher.resume

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import org.junit.jupiter.api.Test

abstract class ResumeFileRepositoryTest {
    abstract fun getRepository(): ResumeFileRepository

    @Test
    fun `file is saved successfully`() {
        val resumeFileBefore = ResumeFile(
                filename = "somefilename.pdf",
                userId = "someUserId",
                fileStream = FileInputStreamFake("some content")
        )

        getRepository().save(resumeFileBefore)

        val resumeFileAfter = getRepository().getByUserId("someUserId")
        assertThat(resumeFileAfter).isNotNull
        assertThat(resumeFileAfter!!.userId).isEqualTo("someUserId")
        var contentSave = ""
        while (true) {
            val stuff = resumeFileAfter.fileStream.read()
            if (stuff == -1)
                break
            contentSave += stuff.toChar()
        }
        assertThat(contentSave).isEqualTo("some content")
    }

    @Test
    fun `when retrieving a file that does not exist, it throws ResumeNotFound exception`() {
        try {
            getRepository().getByUserId("someOtherId")
            fail("Exception should have been thrown")
        } catch (exception: ResumeNotFound) {
            assertThat(exception.message).isEqualTo("Resume for user 'someOtherId' not found")
        }
    }

    @Test
    fun `when calling the function deleteIfExists and the resume exist, it deletes the resume`() {
        val resumeFileBefore = ResumeFile(
                filename = "somefilename.pdf",
                userId = "someUserId",
                fileStream = FileInputStreamFake("some content")
        )

        getRepository().save(resumeFileBefore)

        getRepository().deleteIfExists("someUserId")

        try {
            getRepository().getByUserId("someUserId")
            fail("Exception should have been thrown")
        } catch (exception: ResumeNotFound) {
            assertThat(exception.message).isEqualTo("Resume for user 'someUserId' not found")
        }
    }

    @Test
    fun `when calling the function deleteIfExists and the resume does not exist, it does nothing`() {
        getRepository().deleteIfExists("someUserId")

        try {
            getRepository().getByUserId("someUserId")
            fail("Exception should have been thrown")
        } catch (exception: ResumeNotFound) {
            assertThat(exception.message).isEqualTo("Resume for user 'someUserId' not found")
        }
    }
}
