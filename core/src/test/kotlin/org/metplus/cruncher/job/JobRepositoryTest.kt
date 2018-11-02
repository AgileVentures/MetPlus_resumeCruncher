package org.metplus.cruncher.job

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

abstract class JobRepositoryTest {
    abstract fun getJobRepository(): JobsRepository

    @Test
    fun `when saving a job that did not exist, it return the saved job with id set`() {
        val beforeSave = Job("newjobid", "some title", "some description")
        val afterSave = getJobRepository().save(beforeSave)
        assertThat(afterSave).isEqualToComparingFieldByField(beforeSave)
    }

    @Test
    fun `when saving a job that exist, it return the saved job`() {
        val beforeSave = Job("newjobid", "some title", "some description")
        getJobRepository().save(beforeSave)

        val jobWithNewTitle = beforeSave.copy(title = "some other title", description = "some other description")
        val afterSave = getJobRepository().save(jobWithNewTitle)

        assertThat(afterSave).isEqualToComparingFieldByField(jobWithNewTitle)
    }

    @Test
    fun `when retrieving a job that exist, it return the saved job`() {
        val beforeSave = Job("newjobid", "some title", "some description")
        getJobRepository().save(beforeSave)

        val afterSave = getJobRepository().getById("newjobid")

        assertThat(afterSave).isEqualToComparingFieldByField(beforeSave)
    }

    @Test
    fun `when retrieving a job that does not exist, it return null`() {
        val afterSave = getJobRepository().getById("someIdThatDoesNotExist")

        assertThat(afterSave).isNull()
    }
}