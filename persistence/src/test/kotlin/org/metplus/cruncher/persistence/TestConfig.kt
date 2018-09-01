package org.metplus.cruncher.persistence

import org.metplus.cruncher.persistence.model.SettingsRepositoryImpl
import org.metplus.cruncher.persistence.model.SettingsRepositoryMongo
import org.metplus.cruncher.settings.SettingsRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringBootConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

@SpringBootConfiguration
@EnableMongoRepositories(basePackages = ["org.metplus.cruncher.persistence"])
open class TestConfig {
    @Bean
    open fun getSettingsRepository(@Autowired repo: SettingsRepositoryMongo)
            : SettingsRepository = SettingsRepositoryImpl(repo)
}