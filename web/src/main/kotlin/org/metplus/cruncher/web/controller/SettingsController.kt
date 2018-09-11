package org.metplus.cruncher.web.controller

import org.metplus.cruncher.settings.GetSettings
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/admin/settings")
class SettingsController {
    @Autowired
    private lateinit var getSettings: GetSettings

    @GetMapping("/")
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
            private val id: Int,
            private val appSettings: ApplicationSettingsResponse,
            private val cruncherSettings: CruncherSettings
    )

    data class ApplicationSettingsResponse(
            private val settings: HashMap<String, SettingReponse<*>>
    )

    data class SettingReponse<DataType>(
            private val name: String,
            private val data: DataType
    )

    data class CruncherSettings(
            private val id: Int
    )
}