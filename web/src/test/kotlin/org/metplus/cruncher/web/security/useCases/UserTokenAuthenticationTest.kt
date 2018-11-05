package org.metplus.cruncher.web.security.useCases

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.metplus.cruncher.web.security.services.LocalTokenService
import org.metplus.cruncher.web.security.services.TokenService


class UserTokenAuthenticationTest {
    open class DefaultTokenAuthenticationTest {
        protected lateinit var useCase: UserTokenAuthentication
        protected lateinit var tokenService: TokenService
        protected lateinit var tokens: Array<String?>

        @BeforeEach
        fun before() {
            tokenService = LocalTokenService(30)
            useCase = UserTokenAuthentication(tokenService)
            tokens = arrayOfNulls(2)
            tokens[0] = tokenService.generateToken("1.1.1.1")
            tokens[1] = tokenService.generateToken("2.2.2.2")
            println(tokens[1])
        }
    }

    class AuthenticationSuccessful : DefaultTokenAuthenticationTest() {
        @Test
        @Throws(Exception::class)
        fun correctToken_shouldReturnTrue() {
            val token = tokens[0]
            assertThat(useCase.canLogin(token)).isTrue()
        }
    }

    class AuthenticationFail : DefaultTokenAuthenticationTest() {
        @Test
        @Throws(Exception::class)
        fun incorrectToken_shouldReturnFalse() {
            val token = "3dc6cc5a-b4b7-41a0-b9cb-7906c0e2f40d"
            assertThat(useCase.canLogin(token)).isFalse()
        }
    }
}