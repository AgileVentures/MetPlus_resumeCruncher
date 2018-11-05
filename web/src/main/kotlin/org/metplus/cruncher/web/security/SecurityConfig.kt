package org.metplus.cruncher.web.security


import org.metplus.cruncher.web.security.filters.ApplicationLoginFilter
import org.metplus.cruncher.web.security.filters.ApplicationTokenAuthenticationFilter
import org.metplus.cruncher.web.security.services.LocalTokenService
import org.metplus.cruncher.web.security.services.TokenService
import org.metplus.cruncher.web.security.useCases.UserTryToLogin
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
open class SecurityConfig : WebMvcConfigurer {
    @Value("\${backend.admin.username}")
    internal var username: String? = null
    @Value("\${backend.admin.password}")
    internal var password: String? = null

    override fun addInterceptors(registry: InterceptorRegistry?) {
        registry!!.addInterceptor(applicationLoginFilter()).addPathPatterns("/api/v*/authenticate")
        registry.addInterceptor(applicationTokenAuthenticationFilter())
    }

    private fun applicationLoginFilter(): ApplicationLoginFilter {
        return ApplicationLoginFilter(useCaseUserTryToLogin(), localTokenService())
    }

    private fun applicationTokenAuthenticationFilter(): ApplicationTokenAuthenticationFilter {
        return ApplicationTokenAuthenticationFilter(localTokenService())
    }

    private fun useCaseUserTryToLogin(): UserTryToLogin {
        return UserTryToLogin(username, password)
    }

    @Bean
    open fun localTokenService(): TokenService {
        return LocalTokenService()
    }
}
