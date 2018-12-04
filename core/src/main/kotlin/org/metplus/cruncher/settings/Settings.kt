package org.metplus.cruncher.settings

data class Settings(
        val id: Int,
        val applicationSettings: ApplicationSettings,
        val cruncherSettings: CruncherSettings
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

data class CruncherSettings(
        val database: Map<String, List<String>>,
        val cleanExpressions: List<String>
)