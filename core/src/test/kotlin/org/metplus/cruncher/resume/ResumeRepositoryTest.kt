package org.metplus.cruncher.resume

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.metplus.cruncher.rating.CruncherMetaData

abstract class ResumeRepositoryTest {
    abstract fun getRepository(): ResumeRepository

    @Test
    fun `when saving a resume that did not exist, it return the saved resume with id set`() {
        val beforeSave = Resume(
                "some_file_name.io",
                "someUserId",
                "io-file",
                cruncherData = mapOf()
        )
        val afterSave = getRepository().save(beforeSave)
        assertThat(afterSave).isEqualToComparingFieldByField(beforeSave)
    }

    @Test
    fun `when saving a resume that exist, it return the saved resume`() {
        val beforeSave = Resume(
                "some_file_name.io",
                "someUserId",
                "io-file",
                cruncherData = mapOf()
        )
        getRepository().save(beforeSave)

        val resumeWithNewFile = beforeSave.copy(filename = "some_other_file_name.exe", fileType = "exec")
        val afterSave = getRepository().save(resumeWithNewFile)

        assertThat(afterSave).isEqualToComparingFieldByField(resumeWithNewFile)
    }

    @Test
    fun `when retrieving a resume that exist, it return the saved resume`() {
        val beforeSave = Resume(
                "some_file_name.io",
                "someUserId",
                "io-file",
                cruncherData = mapOf()
        )
        getRepository().save(beforeSave)

        val afterSave = getRepository().getByUserId("someUserId")

        assertThat(afterSave).isEqualToComparingFieldByField(beforeSave)
    }

    @Test
    fun `when retrieving a resume that does not exist, it return null`() {
        val afterSave = getRepository().getByUserId("someUserId")

        assertThat(afterSave).isNull()
    }

    @Test
    fun `when retrieving all resumes and 2 resumes are present, it return an array with the 2`() {
        val resume1 = Resume(
                "some_file_name.io",
                "someUserId",
                "io-file",
                cruncherData = mapOf()
        )
        getRepository().save(resume1)
        val resume2 = Resume(
                "some_file_name1.io",
                "someUserId1",
                "io-file",
                cruncherData = mapOf()
        )
        getRepository().save(resume2)

        val allResumes = getRepository().getAll()

        assertThat(allResumes).isEqualTo(listOf(resume1, resume2))
    }
}