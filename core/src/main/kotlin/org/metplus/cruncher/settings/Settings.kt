package org.metplus.cruncher.settings

data class Settings(
        val id: Int,
        val applicationSettings: HashMap<String, ApplicationSettings>
)

data class ApplicationSettings(
        val settings: HashMap<String, Setting<*>>
)

data class Setting<DataType>(
        val name: String,
        val data: DataType
)