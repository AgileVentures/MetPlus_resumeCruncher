package org.metplus.cruncher.web

import org.metplus.cruncher.settings.GetSettings
import org.metplus.cruncher.settings.SettingsRepository
import org.metplus.cruncher.settings.SettingsRepositoryFake
import org.springframework.boot.SpringBootConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.web.servlet.config.annotation.EnableWebMvc

@SpringBootConfiguration
@EnableWebMvc
open class TestConfiguration {
    @Bean
    open fun getSettingsRepository(): SettingsRepository =  SettingsRepositoryFake()

    @Bean
    open fun getSettings(): GetSettings = GetSettings(getSettingsRepository())
}