package org.metplus.cruncher.persistence.model

import org.metplus.cruncher.settings.*
import java.math.BigInteger

class SettingsRepositoryImpl(
        private val settingsRepository: SettingsRepositoryMongo
) : SettingsRepository {
    override fun save(settings: Settings): Settings {
        return settingsRepository.save(SettingsMongo(
                id = settings.id.toBigInteger(),
                appSettingsMongo = settings.applicationSettings.toSettingsListMongo(),
                cruncherSettingsMongo = settings.cruncherSettings.toCruncherSettingsMongo()
        )).toSettings()
    }

    override fun getAll(): List<Settings> {
        return settingsRepository.findAll().map { it.toSettings() }
    }

    private fun SettingsMongo.toSettings(): Settings {
        return org.metplus.cruncher.settings.Settings(
                id = this.id.intValueExact(),
                applicationSettings = this.appSettingsMongo.toApplicationSettings(),
                cruncherSettings = this.cruncherSettingsMongo.toCruncherSettings()
        )
    }

    private fun SettingsListMongo.toApplicationSettings(): ApplicationSettings {
        val settings: MutableMap<String, Setting<*>> = mutableMapOf()
        this.settings.forEach { (key, value) ->
            settings[key] = value.toSetting()
        }
        return ApplicationSettings(settings = settings as HashMap<String, Setting<*>>)
    }

    private fun <DataType> SettingMongo<DataType>.toSetting(): Setting<DataType> {
        return Setting(
                name = this.name,
                data = this.data
        )
    }

    private fun ApplicationSettings.toSettingsListMongo(): SettingsListMongo {
        val mongoSettings: MutableMap<String, SettingMongo<*>> = mutableMapOf()
        settings.forEach { (key, value) ->
            mongoSettings[key] = value.toMongoSetting()
        }
        return SettingsListMongo(
                BigInteger.valueOf(1),
                mongoSettings as HashMap<String, org.metplus.cruncher.persistence.model.SettingMongo<*>>,
                emptyList())
    }

    private fun <DataType> Setting<DataType>.toMongoSetting(): SettingMongo<DataType> {
        return SettingMongo(name = name, data = data)
    }
}

private fun CruncherSettings.toCruncherSettingsMongo(): CruncherSettingsMongo {
    return CruncherSettingsMongo(
            database = this.database,
            cleanExpressions = this.cleanExpressions
    )
}

private fun CruncherSettingsMongo.toCruncherSettings(): CruncherSettings {
    return CruncherSettings(
            database = this.database,
            cleanExpressions = this.cleanExpressions
    )
}
