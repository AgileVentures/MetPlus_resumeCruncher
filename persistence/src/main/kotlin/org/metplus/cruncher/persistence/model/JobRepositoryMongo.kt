package org.metplus.cruncher.persistence.model

import org.springframework.data.mongodb.repository.MongoRepository

interface JobRepositoryMongo : MongoRepository<JobMongo, String> {
    fun getById(id: String): JobMongo?
}