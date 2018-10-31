package org.metplus.cruncher.settings

class SaveSettings(
        private val settingsRepository: SettingsRepository
) {
    fun process(newSettings: Settings, observer: (Settings) -> Unit) {
        val savedSettings = settingsRepository.save(newSettings)
        observer(savedSettings)
    }
}