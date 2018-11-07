package org.metplus.cruncher.persistence.model.settings

import org.junit.jupiter.api.extension.ExtendWith
import org.metplus.cruncher.persistence.TestConfig
import org.metplus.cruncher.settings.SettingsRepository
import org.metplus.cruncher.settings.SettingsRepositoryTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension

@DataMongoTest
@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [TestConfig::class])
class SettingsMongoRepositoryImplTest : SettingsRepositoryTest() {
    @Autowired
    lateinit var mongoRepository: SettingsRepository

    override fun getRepository(): SettingsRepository {
        return mongoRepository
    }
}