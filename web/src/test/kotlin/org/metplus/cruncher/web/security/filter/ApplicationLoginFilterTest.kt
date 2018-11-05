package org.metplus.cruncher.web.security.filter

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.metplus.cruncher.web.security.filters.ApplicationLoginFilter
import org.metplus.cruncher.web.security.services.LocalTokenService
import org.metplus.cruncher.web.security.services.TokenService
import org.metplus.cruncher.web.security.useCases.UserTryToLogin
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse

class ApplicationLoginFilterTest {
    open class DefaultLoginTest {
        protected lateinit var useCase: UserTryToLogin
        protected lateinit var tokenService: TokenService
        protected lateinit var filter: ApplicationLoginFilter
        protected lateinit var validToken: String

        @BeforeEach
        fun before() {
            useCase = UserTryToLogin("username", "password")
            tokenService = LocalTokenService()
            validToken = tokenService.generateToken("1.1.1.1")
            filter = ApplicationLoginFilter(useCase, tokenService)

        }
    }

    class LoginError : DefaultLoginTest() {
        @Test
        @Throws(Exception::class)
        fun invalidCredentials_shouldReturnAuthenticationError() {
            val request = createRequest("1.1.1.1", "u", "p")
            val response = MockHttpServletResponse()
            assertFalse(filter.preHandle(request, response, filter))
            assertThat(response.status).isEqualTo(401)
        }

        @Test
        @Throws(Exception::class)
        fun noCredentialsPresentCredentials_shouldReturnAuthenticationError() {
            val request = MockHttpServletRequest()
            val response = MockHttpServletResponse()
            assertFalse(filter.preHandle(request, response, filter))
            assertThat(response.status).isEqualTo(401)
        }
    }

    class LoginSuccessfull : DefaultLoginTest() {

        @Test
        @Throws(Exception::class)
        fun usernameAndLoginCorrect_shouldReturnGeneratedToken() {
            val request = createRequest("1.1.1.1", "username", "password")
            val response = MockHttpServletResponse()
            assertFalse(filter.preHandle(request, response, filter))
            assertThat(response.status).isEqualTo(200)
            assertThat(response.contentAsString).isEqualTo("{\"token\": \"$validToken\"}")
            assertThat(response.contentType).isEqualTo("application/json")
        }
    }
}

private fun createRequest(remoteAddr: String, username: String, password: String): MockHttpServletRequest {
    val request = MockHttpServletRequest()
    request.remoteAddr = remoteAddr
    request.addHeader("X-Auth-Username", username)
    request.addHeader("X-Auth-Password", password)
    return request
}