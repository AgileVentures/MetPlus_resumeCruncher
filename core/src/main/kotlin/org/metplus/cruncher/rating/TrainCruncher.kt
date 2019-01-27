package org.metplus.cruncher.rating

import org.metplus.cruncher.settings.ApplicationSettings
import org.metplus.cruncher.settings.CruncherSettings
import org.metplus.cruncher.settings.Settings
import org.metplus.cruncher.settings.SettingsRepository
import org.slf4j.LoggerFactory

class TrainCruncher(
        private val settingsRepository: SettingsRepository,
        private val cruncher: Cruncher,
        private val defaultCruncherSettings: CruncherSettings
) {
    private val logger = LoggerFactory.getLogger(TrainCruncher::class.java)
    fun process(observer: TrainCruncherObserver) {
        logger.debug("going to train cruncher: ${cruncher.getCruncherName()}")
        var settings = settingsRepository.getAll().firstOrNull()

        if (settings == null) {
            logger.warn("Application incorrectly created, going to created new set of settings")
            settings = settingsRepository.save(Settings(1, ApplicationSettings(hashMapOf()), defaultCruncherSettings))
        }

        cruncher.train(settings.cruncherSettings.database)
        observer.onSuccess()
    }
}

interface TrainCruncherObserver {
    fun onSuccess()
}