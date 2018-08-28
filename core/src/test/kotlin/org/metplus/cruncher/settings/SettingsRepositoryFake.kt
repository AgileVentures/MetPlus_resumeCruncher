package org.metplus.cruncher.settings

class SettingsRepositoryFake(
        private val settings: MutableList<Settings> = mutableListOf()
): SettingsRepository {
    override fun getAll(): List<Settings> {
        return settings
    }

}
