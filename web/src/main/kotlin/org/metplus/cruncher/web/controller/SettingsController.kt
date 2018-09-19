package org.metplus.cruncher.web.controller

import org.metplus.cruncher.settings.GetSettings
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/admin/settings")
class SettingsController(
        @Autowired private var getSettings: GetSettings
) {

    @GetMapping("/")
    @ResponseBody
    fun getSettings(): SettingsResponse {
        var settingsResponse: SettingsResponse? = null
        getSettings.process {
            val applicationSettings = mutableMapOf<String, SettingReponse<*>>()
            it.applicationSettings.settings.forEach { key, setting -> applicationSettings[key] = SettingReponse(key, setting) }
            settingsResponse = SettingsResponse(
                    id = it.id,
                    appSettings = ApplicationSettingsResponse(
                            settings = applicationSettings as HashMap<String, SettingReponse<*>>
                    ),
                    cruncherSettings = CruncherSettings(1)
            )
        }

        return settingsResponse!!
    }

    data class SettingsResponse(
            val id: Int,
            val appSettings: ApplicationSettingsResponse,
            val cruncherSettings: CruncherSettings
    )

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