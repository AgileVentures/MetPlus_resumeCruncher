package org.metplus.cruncher.settings

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

abstract class SettingsRepositoryTest {
    abstract fun getRepository(): SettingsRepository

    @Test
    fun `when no settings are available it returns empty list`() {
        assertThat(getRepository().getAll()).isEmpty()
    }
}