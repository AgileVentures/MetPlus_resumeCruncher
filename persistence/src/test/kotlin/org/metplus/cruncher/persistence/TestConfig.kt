package org.metplus.cruncher.persistence

import com.mongodb.MongoClient
import com.mongodb.ServerAddress
import org.metplus.cruncher.job.JobsRepository
import org.metplus.cruncher.persistence.model.JobRepositoryImpl
import org.metplus.cruncher.persistence.model.JobRepositoryMongo
import org.metplus.cruncher.persistence.model.ResumeRepositoryImpl
import org.metplus.cruncher.persistence.model.ResumeRepositoryMongo
import org.metplus.cruncher.persistence.model.SettingsRepositoryImpl
import org.metplus.cruncher.persistence.model.SettingsRepositoryMongo
import org.metplus.cruncher.resume.ResumeRepository
import org.metplus.cruncher.settings.SettingsRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.data.mongodb.config.AbstractMongoConfiguration
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

@SpringBootConfiguration
@EnableMongoRepositories(basePackages = ["org.metplus.cruncher.persistence"])
open class TestConfig {
    @Bean
    open fun getSettingsRepository(@Autowired repo: SettingsRepositoryMongo)
            : SettingsRepository = SettingsRepositoryImpl(repo)

    @Bean
    open fun getJobRepository(@Autowired repo: JobRepositoryMongo)
            : JobsRepository = JobRepositoryImpl(repo)

    @Bean
    open fun getResumeRepository(@Autowired repo: ResumeRepositoryMongo)
            : ResumeRepository = ResumeRepositoryImpl(repo)

//    @Bean
//    open fun getMongoConfiguration(@Autowired mongoClient: MongoClient) : AbstractMongoConfiguration = object : AbstractMongoConfiguration(){
//        override fun mongoClient(): MongoClient {
//            return mongoClient
//        }
//
//        override fun getDatabaseName(): String {
//            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//        }
//
//    }
}