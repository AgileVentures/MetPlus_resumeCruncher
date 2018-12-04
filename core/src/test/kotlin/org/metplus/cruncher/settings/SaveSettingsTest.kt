package org.metplus.cruncher.settings

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

internal class SaveSettingsTest {
    @Test
    fun `When saving existing settings, return saved object`() {
        val settingsRepository = SettingsRepositoryFake()
        val settings = emptySettingsConstructor(1)
        val saveSettings = SaveSettings(settingsRepository)
        var wasCalled = false
        settingsRepository.save(settings)
        val newRepository = Settings(1,
                ApplicationSettings(hashMapOf(
                        "some setting" to Setting("some setting", "some value"))),
                CruncherSettings(hashMapOf(), listOf()))

        saveSettings.process(newRepository) { result: Settings ->
            Assertions.assertThat(result)
                    .isNotNull
                    .isEqualToComparingFieldByField(newRepository)
            wasCalled = true
        }

        Assertions.assertThat(wasCalled).isTrue()
        Assertions.assertThat(settingsRepository.getAll().first())
                .isNotNull
                .isEqualToComparingFieldByField(newRepository)
    }

    @Test
    fun `When saving new settings, return saved object`() {
        val settingsRepository = SettingsRepositoryFake()
        val saveSettings = SaveSettings(settingsRepository)
        var wasCalled = false
        val newRepository = Settings(1,
                ApplicationSettings(hashMapOf(
                        "some setting" to Setting("some setting", "some value"))),
                CruncherSettings(hashMapOf(), listOf()))

        saveSettings.process(newRepository) { result: Settings ->
            Assertions.assertThat(result)
                    .isNotNull
                    .isEqualToComparingFieldByField(newRepository)
            wasCalled = true
        }

        Assertions.assertThat(wasCalled).isTrue()
        Assertions.assertThat(settingsRepository.getAll().first())
                .isNotNull
                .isEqualToComparingFieldByField(newRepository)
    }
}