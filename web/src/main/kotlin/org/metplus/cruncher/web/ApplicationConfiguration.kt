package org.metplus.cruncher.web

import org.metplus.cruncher.job.CreateJob
import org.metplus.cruncher.job.JobsRepository
import org.metplus.cruncher.persistence.model.JobRepositoryImpl
import org.metplus.cruncher.persistence.model.JobRepositoryMongo
import org.metplus.cruncher.persistence.model.SettingsRepositoryImpl
import org.metplus.cruncher.persistence.model.SettingsRepositoryMongo
import org.metplus.cruncher.settings.GetSettings
import org.metplus.cruncher.settings.SaveSettings
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringBootConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

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
    open fun createJob(
            @Autowired jobsRepository: JobsRepository
    ): CreateJob = CreateJob(jobsRepository)
}