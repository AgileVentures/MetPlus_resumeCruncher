package org.metplus.cruncher.web.security.services

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.Mockito.reset
import java.time.Clock
import java.time.Instant


open class LocalTokenServiceTestDefaultLocalTokenServiceTest {
    internal lateinit var clock: Clock
    internal lateinit var now: Instant
    internal lateinit var afterTimeout: Instant
    protected lateinit var service: LocalTokenService
    protected lateinit var generatedKey: String

    @BeforeEach
    fun before() {
        clock = mock(Clock::class.java)
        now = Instant.now()
        afterTimeout = now.plusSeconds(1800)
        `when`(clock.instant()).thenReturn(now)

        service = LocalTokenService(clock, 1800)
        generatedKey = service.generateToken("1.1.1.1")
    }
}

class LocalTokenServiceTestValidToken : LocalTokenServiceTestDefaultLocalTokenServiceTest() {
    @Test
    fun validToken_shouldReturnTrue() {
        assertThat(service.isValid(generatedKey)).isTrue()
    }

    @Test
    fun invalidToken_shouldReturnFalse() {
        assertThat(service.isValid("9367f48e-94d3-4b3b-8cbf-55cf61e58fe2")).isFalse()
    }

    @Test
    fun nullToken_shouldReturnFalse() {
        assertThat(service.isValid(null)).isFalse()
    }

    @Test
    fun tokenNotUUID_shouldReturnFalse() {
        assertThat(service.isValid("123")).isFalse()
    }

    @Test
    fun timeoutReached_shouldReturnFalse() {
        reset(clock)
        `when`(clock.instant()).thenReturn(afterTimeout)
        assertThat(service.isValid(generatedKey)).isFalse()
    }

    @Test
    @Throws(Exception::class)
    fun timeoutNotReached_shouldReturnTrue() {
        reset(clock)
        `when`(clock.instant()).thenReturn(afterTimeout.minusSeconds(1))
        assertThat(service.isValid(generatedKey)).isTrue()
    }
}

class LocalTokenServiceTestGenerateToken : LocalTokenServiceTestDefaultLocalTokenServiceTest() {
    @Test
    @Throws(Exception::class)
    fun tokenGenerated_shouldBeValid() {
        val token = service.generateToken("123.123.123.123")
        assertThat(service.isValid(token)).isTrue()
    }

    @Test
    @Throws(Exception::class)
    fun tokenGeneratedTwiceForIPAddress_shouldReturnSameKey() {
        val token = service.generateToken("2.2.2.2")
        assertThat(service.generateToken("2.2.2.2")).isEqualTo(token)
    }

    @Test
    @Throws(Exception::class)
    fun tokenGeneratedTwiceForIPAddressAfterTimeout_shouldReturnDifferentKey() {
        service = LocalTokenService(0)
        val token = service.generateToken("2.2.2.2")
        assertThat(service.generateToken("2.2.2.2")).isNotEqualTo(token)
    }

    @Test
    @Throws(Exception::class)
    fun tokenGeneratedTwiceForIPAddressAfterTimeout_shouldNotIncreaseTheNumberOfTokens() {
        service = LocalTokenService(0)
        val token = service.generateToken("2.2.2.2")
        val numberOfToken = service.totalNumberTokens()
        service.generateToken("2.2.2.2")
        assertThat(service.totalNumberTokens()).isEqualTo(numberOfToken)
    }
}