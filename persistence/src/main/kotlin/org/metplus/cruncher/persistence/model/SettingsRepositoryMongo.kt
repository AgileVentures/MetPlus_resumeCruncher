package org.metplus.cruncher.persistence.model

import org.springframework.data.mongodb.repository.MongoRepository

interface SettingsRepositoryMongo : MongoRepository<SettingsMongo, Int>