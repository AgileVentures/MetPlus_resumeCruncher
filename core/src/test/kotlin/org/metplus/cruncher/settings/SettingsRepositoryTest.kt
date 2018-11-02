package org.metplus.cruncher.settings

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

abstract class SettingsRepositoryTest {
    abstract fun getRepository(): SettingsRepository

    @Test
    fun `when no settings are available it returns empty list`() {
        assertThat(getRepository().getAll()).isEmpty()
    }

    @Test
    fun `when settings are available it returns them`() {
        val settings = Settings(
                1,
                ApplicationSettings(
                        hashMapOf(
                                "color" to Setting<Int>("green", 1),
                                "key" to Setting<String>("key", "asdafdasasdf")
                        )
                )
        )
        val resultAfterSave = getRepository().save(settings)

        assertThat(getRepository().getAll().firstOrNull())
                .isEqualToComparingFieldByField(resultAfterSave)

    }

    @Test
    fun `when settings are available it save updates a setting if the setting with a specific id already exists`() {
        val initialSetting = getRepository().save(Settings(
                1,
                ApplicationSettings(
                        hashMapOf(
                                "color" to Setting("green", 1),
                                "key" to Setting("key", "asdafdasasdf")
                        )
                )
        ))

        val updatedSetting = initialSetting.copy(applicationSettings =
        ApplicationSettings(
                hashMapOf(
                        "some_setting" to Setting("some name", "some data")
                )
        )
        )

        val resultAfterSave = getRepository().save(updatedSetting)

        assertThat(getRepository().getAll()).hasSize(1)
        assertThat(getRepository().getAll().firstOrNull())
                .isEqualToComparingFieldByField(resultAfterSave)

    }
}