package org.metplus.cruncher.settings

class GetSettings(
        private val settingsRepository: SettingsRepository
) {
    fun process(observer: (Settings) -> Unit) {
        var settings = settingsRepository.getAll().firstOrNull()

        if (settings == null) {
            settings = Settings(1, ApplicationSettings(hashMapOf()))
        }

        observer(settings)
    }
}
