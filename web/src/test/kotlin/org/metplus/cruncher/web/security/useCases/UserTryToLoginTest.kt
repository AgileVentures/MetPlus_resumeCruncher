package org.metplus.cruncher.web.security.useCases

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


class UserTryToLoginTest {
    open class DefaultLoginTest {
        protected lateinit var useCase: UserTryToLogin

        @BeforeEach
        fun before() {
            useCase = UserTryToLogin("username", "password")
        }
    }

    class LoginSuccessfull : DefaultLoginTest() {
        @Test
        @Throws(Exception::class)
        fun correctUserNameAndPassword_shouldReturnTrue() {
            assertThat(useCase.canUserLogin("username", "password")).isTrue()
        }

        @Test
        @Throws(Exception::class)
        fun changeUserAndPasswordOnConstructor_shouldReturnTrue() {
            useCase = UserTryToLogin("test", "testing")
            assertThat(useCase.canUserLogin("test", "testing")).isTrue()
        }
    }

    class LoginError : DefaultLoginTest() {
        @Test
        @Throws(Exception::class)
        fun incorrectUserNameAndPassword_shouldReturnFalse() {
            assertThat(useCase.canUserLogin("invalid_user", "invalid_password")).isFalse()
        }

        @Test
        @Throws(Exception::class)
        fun incorrectUserName_shouldReturnFalse() {
            assertThat(useCase.canUserLogin("invalid_user", "password")).isFalse()
        }

        @Test
        @Throws(Exception::class)
        fun incorrectPassword_shouldReturnFalse() {
            assertThat(useCase.canUserLogin("username", "invalid_password")).isFalse()
        }

        @Test
        @Throws(Exception::class)
        fun nullUsername_shouldReturnFalse() {
            assertThat(useCase.canUserLogin(null, "")).isFalse()
        }

        @Test
        @Throws(Exception::class)
        fun nullPassword_shouldReturnFalse() {
            assertThat(useCase.canUserLogin("username", null)).isFalse()
        }
    }
}