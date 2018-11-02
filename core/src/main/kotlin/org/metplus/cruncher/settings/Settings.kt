package org.metplus.cruncher.settings

data class Settings(
        val id: Int,
        val applicationSettings: ApplicationSettings
) {
    fun getApplicationSetting(setting: String): Setting<*> {
        return Setting("bamm", "badam")
    }
}

data class ApplicationSettings(
        val settings: HashMap<String, Setting<*>>
)

data class Setting<DataType>(
        val name: String,
        val data: DataType
)