package org.metplus.cruncher.persistence

import org.metplus.cruncher.job.JobsRepository
import org.metplus.cruncher.persistence.model.*
import org.metplus.cruncher.resume.ResumeRepository
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

    @Bean
    open fun getJobRepository(@Autowired repo: JobRepositoryMongo)
            : JobsRepository = JobRepositoryImpl(repo)

    @Bean
    open fun getResumeRepository(@Autowired repo: ResumeRepositoryMongo)
            : ResumeRepository = ResumeRepositoryImpl(repo)
}