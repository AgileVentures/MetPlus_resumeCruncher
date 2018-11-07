package org.metplus.cruncher.web

import com.mongodb.MongoClient
import com.mongodb.MongoClientOptions
import com.mongodb.MongoCredential
import com.mongodb.ServerAddress
import com.mongodb.WriteConcern
import org.metplus.cruncher.job.CreateJob
import org.metplus.cruncher.job.JobsRepository
import org.metplus.cruncher.job.UpdateJob
import org.metplus.cruncher.persistence.model.JobRepositoryImpl
import org.metplus.cruncher.persistence.model.JobRepositoryMongo
import org.metplus.cruncher.persistence.model.ResumeFileRepositoryImpl
import org.metplus.cruncher.persistence.model.ResumeRepositoryImpl
import org.metplus.cruncher.persistence.model.ResumeRepositoryMongo
import org.metplus.cruncher.persistence.model.SettingsRepositoryImpl
import org.metplus.cruncher.persistence.model.SettingsRepositoryMongo
import org.metplus.cruncher.resume.ResumeFileRepository
import org.metplus.cruncher.resume.ResumeRepository
import org.metplus.cruncher.resume.UploadResume
import org.metplus.cruncher.settings.GetSettings
import org.metplus.cruncher.settings.SaveSettings
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringBootConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.MongoDbFactory
import org.springframework.data.mongodb.config.AbstractMongoConfiguration
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.convert.MappingMongoConverter
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
import java.util.ArrayList

@SpringBootConfiguration
@EnableMongoRepositories(basePackages = ["org.metplus.cruncher.persistence"])
open class ApplicationConfiguration {
    @Bean
    open fun getSettingsRepository(
            @Autowired settingsRepositoryMongo: SettingsRepositoryMongo
    ) = SettingsRepositoryImpl(settingsRepository = settingsRepositoryMongo)

    @Bean
    open fun getSettings(
            @Autowired settingsRepository: SettingsRepositoryImpl
    ) = GetSettings(settingsRepository = settingsRepository)

    @Bean
    open fun saveSettings(
            @Autowired settingsRepository: SettingsRepositoryImpl
    ) = SaveSettings(settingsRepository = settingsRepository)

    @Bean
    open fun getJobRepository(
            @Autowired jobRepositoryMongo: JobRepositoryMongo
    ): JobsRepository = JobRepositoryImpl(jobRepository = jobRepositoryMongo)

    @Bean
    open fun getResumeRepository(
            @Autowired resumeRepositoryMongo: ResumeRepositoryMongo
    ): ResumeRepository = ResumeRepositoryImpl(resumeRepositoryMongo)

    @Bean
    open fun getResumeFileRepository(
            @Autowired mongoDbFactory: MongoDbFactory,
            @Autowired mappingMongoConverter: MappingMongoConverter
    ): ResumeFileRepository = ResumeFileRepositoryImpl(
            mongoDbFactory = mongoDbFactory,
            mappingMongoConverter = mappingMongoConverter
    )

    @Bean
    open fun createJob(
            @Autowired jobsRepository: JobsRepository
    ): CreateJob = CreateJob(jobsRepository)

    @Bean
    open fun updateJob(
            @Autowired jobsRepository: JobsRepository
    ): UpdateJob = UpdateJob(jobsRepository)

    @Bean
    open fun uploadResume(
            @Autowired resumeRepository: ResumeRepository,
            @Autowired resumeFileRepository: ResumeFileRepository
    ): UploadResume = UploadResume(resumeRepository, resumeFileRepository)
}

@Configuration
open class SpringMongoConfig : AbstractMongoConfiguration() {
    @Autowired
    private lateinit var dbConfig: DatabaseConfig

    override fun getDatabaseName(): String {
        return dbConfig.name
    }


    @Throws(Exception::class)
    private fun withAuthentication(): MongoClient {
        val a = MongoCredential.createCredential(dbConfig.username,
                databaseName,
                dbConfig.password.toCharArray())
        val arr = ArrayList<MongoCredential>()
        arr.add(a)
        val addr = ServerAddress(dbConfig.host,
                dbConfig.port)

        return MongoClient(addr,
                a,
                MongoClientOptions.Builder().writeConcern(WriteConcern.ACKNOWLEDGED).build())
    }

    @Throws(Exception::class)
    private fun withoutAuthentication(): MongoClient {
        val addr = ServerAddress(dbConfig.host,
                dbConfig.port)
        return MongoClient(addr,
                MongoClientOptions.Builder().writeConcern(WriteConcern.ACKNOWLEDGED).build())
    }

    override fun getMappingBasePackages(): Collection<String> {
        return setOf("org.metplus.curriculum.database.domain")
    }

    // ---------------------------------------------------- MongoTemplate

    override fun mongoClient(): MongoClient? {
        try {
            logger.info("mongo connection to database: " + dbConfig.host + ":" + dbConfig.port + "/" + dbConfig.name)
            logger.info("mongo connection uri to database: " + dbConfig.uri)
            return if (dbConfig.asAuthentication())
                withAuthentication()
            else
                withoutAuthentication()
        } catch (exp: Exception) {
            return null
        }

    }

    @Bean
    @Throws(Exception::class)
    override fun mongoTemplate(): MongoTemplate {
        return MongoTemplate(mongoClient()!!, databaseName)
    }

    companion object {

        private val logger = LoggerFactory.getLogger(SpringMongoConfig::class.java)
    }
}