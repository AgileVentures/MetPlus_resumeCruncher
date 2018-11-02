package org.metplus.cruncher.settings

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class GetSettingsTest {
    @Test
    fun `When no settings are present, return empty object`() {
        val settingsRepository = SettingsRepositoryFake()
        val settings = Settings(
                1, ApplicationSettings(hashMapOf()))
        val getSettings = GetSettings(settingsRepository)
        var wasCalled = false

        getSettings.process { result: Settings ->
            assertThat(result)
                    .isNotNull
                    .isEqualToComparingFieldByField(settings)
            wasCalled = true
        }

        assertThat(wasCalled).isTrue()
    }

    @Test
    fun `When settings are present, return the settings`() {
        val settingsRepository = SettingsRepositoryFake()
        val settings = Settings(
                1, ApplicationSettings(hashMapOf()))
        val getSettings = GetSettings(settingsRepository)
        var wasCalled = false

        getSettings.process { result: Settings ->
            assertThat(result)
                    .isNotNull
                    .isEqualToComparingFieldByField(settings)
            wasCalled = true
        }

        assertThat(wasCalled).isTrue()
    }
}