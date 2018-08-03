package org.metplus.cruncher.settings

abstract class SettingsRepositoryTest {
    abstract fun getRepository(): SettingsRepository

    fun `when no settings are available it returns empty list`() {
        assertThat(getRepository().getAll())
    }
}