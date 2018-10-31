package org.metplus.cruncher.web.controller

import org.metplus.cruncher.settings.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/admin/settings")
class SettingsController(
        @Autowired private var getSettings: GetSettings,
        @Autowired private var saveSettings: SaveSettings
) {

    @GetMapping("/")
    @ResponseBody
    fun getSettings(): SettingsResponse {
        var settingsResponse: SettingsResponse? = null
        getSettings.process {
            settingsResponse = it.toSettingsResponse()
        }

        return settingsResponse!!
    }

    @PostMapping
    @ResponseBody
    fun saveSettings(@RequestBody settings: SettingsResponse): SettingsResponse {
        var settingsResponse: SettingsResponse? = null
        saveSettings.process(settings.toSettings()) {
            settingsResponse = it.toSettingsResponse()
        }

        return settingsResponse!!
    }


    data class SettingsResponse(
            val id: Int,
            val appSettings: ApplicationSettingsResponse,
            val cruncherSettings: CruncherSettings
    ) {
        fun toSettings(): Settings {
            val applicationSettings = mutableMapOf<String, Setting<*>>()
            this.appSettings.settings.forEach { key, setting ->
                applicationSettings[key] = Setting(key, setting.data)
            }
            return Settings(
                    id = id,
                    applicationSettings = ApplicationSettings(
                            settings = applicationSettings as HashMap<String, Setting<*>>
                    )
            )
        }
    }

    data class ApplicationSettingsResponse(
            val settings: HashMap<String, SettingReponse<*>>
    )

    data class SettingReponse<DataType>(
            val name: String,
            val data: DataType
    )

    data class CruncherSettings(
            val id: Int
    )
}

fun Settings.toSettingsResponse(): SettingsController.SettingsResponse? {
    val applicationSettings = mutableMapOf<String, SettingsController.SettingReponse<*>>()
    this.applicationSettings.settings.forEach { key, setting ->
        applicationSettings[key] = SettingsController.SettingReponse(key, setting.data)
    }
    return SettingsController.SettingsResponse(
            id = id,
            appSettings = SettingsController.ApplicationSettingsResponse(
                    settings = applicationSettings as HashMap<String, SettingsController.SettingReponse<*>>
            ),
            cruncherSettings = SettingsController.CruncherSettings(1)
    )
}