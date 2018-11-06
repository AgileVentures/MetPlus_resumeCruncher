package org.metplus.cruncher.resume

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

abstract class ResumeRepositoryTest {
    abstract fun getRepository(): ResumeRepository

    @Test
    fun `when saving a resume that did not exist, it return the saved resume with id set`() {
        val beforeSave = Resume("some_file_name.io", "someUserId", "io-file")
        val afterSave = getRepository().save(beforeSave)
        assertThat(afterSave).isEqualToComparingFieldByField(beforeSave)
    }

    @Test
    fun `when saving a resume that exist, it return the saved resume`() {
        val beforeSave = Resume("some_file_name.io", "someUserId", "io-file")
        getRepository().save(beforeSave)

        val resumeWithNewFile = beforeSave.copy(filename = "some_other_file_name.exe", fileType = "exec")
        val afterSave = getRepository().save(resumeWithNewFile)

        assertThat(afterSave).isEqualToComparingFieldByField(resumeWithNewFile)
    }

    @Test
    fun `when retrieving a resume that exist, it return the saved resume`() {
        val beforeSave = Resume("some_file_name.io", "someUserId", "io-file")
        getRepository().save(beforeSave)

        val afterSave = getRepository().getByUserId("someUserId")

        assertThat(afterSave).isEqualToComparingFieldByField(beforeSave)
    }

    @Test
    fun `when retrieving a resume that does not exist, it return null`() {
        val afterSave = getRepository().getByUserId("someUserId")

        assertThat(afterSave).isNull()
    }
}