package org.metplus.cruncher.settings

class SettingsRepositoryFake(
        private val settingsList: MutableList<Settings> = mutableListOf()
): SettingsRepository {
    override fun save(settings: Settings): Settings {
        val settingsToSave = settings.copy()
        val existingVersion = settingsList.find { it.id == settings.id }
        if (existingVersion != null){
            settingsList.remove(existingVersion)
        }
        settingsList.add(settingsToSave)
        return settingsToSave
    }

    override fun getAll(): List<Settings> {
        return settingsList
    }

    fun removeAll() {
        settingsList.clear()
    }
}
