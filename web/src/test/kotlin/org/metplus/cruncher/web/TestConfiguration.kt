package org.metplus.cruncher.web

import org.metplus.cruncher.job.CreateJob
import org.metplus.cruncher.job.JobRepositoryFake
import org.metplus.cruncher.job.JobsRepository
import org.metplus.cruncher.settings.GetSettings
import org.metplus.cruncher.settings.SaveSettings
import org.metplus.cruncher.settings.SettingsRepository
import org.metplus.cruncher.settings.SettingsRepositoryFake
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringBootConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.web.servlet.config.annotation.EnableWebMvc

@SpringBootConfiguration
@EnableWebMvc
open class TestConfiguration {
    @Bean
    open fun getSettingsRepository(): SettingsRepository = SettingsRepositoryFake()

    @Bean
    open fun getJobRepository(): JobsRepository = JobRepositoryFake()

    @Bean
    open fun getSettings(): GetSettings = GetSettings(getSettingsRepository())

    @Bean
    open fun saveSettings(): SaveSettings = SaveSettings(getSettingsRepository())

    @Bean
    open fun createJob(@Autowired jobsRepository: JobsRepository): CreateJob = CreateJob(jobsRepository)
}