package org.metplus.cruncher.persistence.model

import org.metplus.cruncher.settings.ApplicationSettings
import org.metplus.cruncher.settings.Setting
import org.metplus.cruncher.settings.Settings
import org.metplus.cruncher.settings.SettingsRepository

class SettingsRepositoryImpl(
        private val settingsRepository: SettingsRepositoryMongo
): SettingsRepository {
    override fun getAll(): List<Settings> {
        return settingsRepository.findAll().map { it.toSettings() }
    }

    private fun org.metplus.cruncher.persistence.model.Settings.toSettings(): org.metplus.cruncher.settings.Settings {
        return org.metplus.cruncher.settings.Settings(
                id = this.id.intValueExact(),
                applicationSettings = this.appSettings.toApplicationSettings()
        )
    }

    private fun SettingsList.toApplicationSettings(): ApplicationSettings {
        val settings: MutableMap<String, Setting<*>> = mutableMapOf()
        this.settings.forEach {
            (key, value) -> settings[key] = value.toSetting()
        }
        return ApplicationSettings(settings = settings as HashMap<String, Setting<*>>)
    }

    private fun <DataType> org.metplus.cruncher.persistence.model.Setting<DataType>.toSetting(): org.metplus.cruncher.settings.Setting<DataType> {
        return Setting<DataType>(
                name = this.name,
                data = this.data
        )
    }
}


