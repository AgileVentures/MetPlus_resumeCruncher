package org.metplus.cruncher.web.security.filter

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.metplus.cruncher.web.security.filters.ApplicationTokenAuthenticationFilter
import org.metplus.cruncher.web.security.services.LocalTokenService
import org.metplus.cruncher.web.security.services.TokenService
import org.metplus.cruncher.web.security.useCases.UserTokenAuthentication
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse

open class ApplicationTokenAuthenticationFilterDefaultLoginTest {
    protected lateinit var useCase: UserTokenAuthentication
    protected lateinit var tokenService: TokenService
    protected lateinit var filter: ApplicationTokenAuthenticationFilter
    protected lateinit var validToken: String

    @BeforeEach
    fun before() {
        tokenService = LocalTokenService()
        useCase = UserTokenAuthentication(tokenService)
        validToken = tokenService.generateToken("1.1.1.1")
        filter = ApplicationTokenAuthenticationFilter(tokenService)
    }
}

class ApplicationTokenAuthenticationFilterLoginError : ApplicationTokenAuthenticationFilterDefaultLoginTest() {
    @Test
    @Throws(Exception::class)
    fun invalidToken_shouldReturnAuthenticationError() {
        val request = createRequest("1.1.1.1", "fe02176d-4c64-4404-b734-94232a4d54cc")
        val response = MockHttpServletResponse()
        assertThat(filter.preHandle(request, response, filter)).isFalse()
        assertThat(response.status).isEqualTo(401)
    }

    @Test
    @Throws(Exception::class)
    fun noTokenPresent_shouldReturnAuthenticationError() {
        val request = MockHttpServletRequest()
        val response = MockHttpServletResponse()
        assertThat(filter.preHandle(request, response, filter)).isFalse()
        assertThat(response.status).isEqualTo(401)
    }
}

class ApplicationTokenAuthenticationFilterLoginSuccessfull : ApplicationTokenAuthenticationFilterDefaultLoginTest() {

    @Test
    @Throws(Exception::class)
    fun tokenCorrect_shouldReturnGeneratedToken() {
        val request = createRequest("1.1.1.1", validToken)
        val response = MockHttpServletResponse()
        assertThat(filter.preHandle(request, response, filter)).isTrue()
    }
}

private fun createRequest(remoteAddr: String, token: String): MockHttpServletRequest {
    val request = MockHttpServletRequest()
    request.remoteAddr = remoteAddr
    request.addHeader("X-Auth-Token", token)
    return request
}