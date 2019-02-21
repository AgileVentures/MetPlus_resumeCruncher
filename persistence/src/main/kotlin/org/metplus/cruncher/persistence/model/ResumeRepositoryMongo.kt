package org.metplus.cruncher.persistence.model

import org.springframework.data.mongodb.repository.MongoRepository

interface ResumeRepositoryMongo : MongoRepository<ResumeMongo, String> {
    fun getByUserId(id: String): ResumeMongo?
}