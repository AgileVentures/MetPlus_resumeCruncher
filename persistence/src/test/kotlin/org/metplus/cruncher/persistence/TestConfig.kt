package org.metplus.cruncher.persistence

import org.metplus.cruncher.persistence.model.SettingsRepositoryImpl
import org.metplus.cruncher.persistence.model.SettingsRepositoryMongo
import org.springframework.boot.SpringBootConfiguration

@SpringBootConfiguration
open class TestConfig {
    fun getSettingsRepository(repo: SettingsRepositoryMongo) = SettingsRepositoryImpl(repo)
}