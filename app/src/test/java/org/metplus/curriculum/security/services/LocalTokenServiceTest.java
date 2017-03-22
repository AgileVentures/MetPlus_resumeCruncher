package org.metplus.curriculum.security.services;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.metplus.curriculum.test.BeforeAfterInterface;
import org.metplus.curriculum.test.BeforeAfterRule;

import java.time.Clock;
import java.time.Instant;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;


@RunWith(Suite.class)
@Suite.SuiteClasses({LocalTokenServiceTest.ValidToken.class,
        LocalTokenServiceTest.GenerateToken.class})
public class LocalTokenServiceTest {

    public static class DefaultLocalTokenServiceTest implements BeforeAfterInterface {
        Clock clock;
        Instant now;
        Instant afterTimeout;
        @Rule
        public BeforeAfterRule beforeAfter = new BeforeAfterRule(this);
        protected LocalTokenService service;
        protected String generatedKey;

        @Override
        public void after() {
        }

        @Override
        public void before() {
            clock = mock(Clock.class);
            now = Instant.now();
            afterTimeout = now.plusSeconds(1800);
            when(clock.instant()).thenReturn(now);

            service = new LocalTokenService(clock, 1800);
            generatedKey = service.generateToken("1.1.1.1");
        }
    }

    public static class ValidToken extends DefaultLocalTokenServiceTest {
        @Test
        public void validToken_shouldReturnTrue() throws Exception {
            assertTrue(service.isValid(generatedKey));
        }

        @Test
        public void invalidToken_shouldReturnFalse() throws Exception {
            assertFalse(service.isValid("9367f48e-94d3-4b3b-8cbf-55cf61e58fe2"));
        }

        @Test
        public void nullToken_shouldReturnFalse() throws Exception {
            assertFalse(service.isValid(null));
        }

        @Test
        public void tokenNotUUID_shouldReturnFalse() throws Exception {
            assertFalse(service.isValid("123"));
        }

        @Test
        public void timeoutReached_shouldReturnFalse() throws Exception {
            reset(clock);
            when(clock.instant()).thenReturn(afterTimeout);
            assertFalse(service.isValid(generatedKey));
        }

        @Test
        public void timeoutNotReached_shouldReturnTrue() throws Exception {
            reset(clock);
            when(clock.instant()).thenReturn(afterTimeout.minusSeconds(1));
            assertTrue(service.isValid(generatedKey));
        }
    }

    public static class GenerateToken extends DefaultLocalTokenServiceTest {
        @Test
        public void tokenGenerated_shouldBeValid() throws Exception {
            String token = service.generateToken("123.123.123.123");
            assertTrue(service.isValid(token));
        }

        @Test
        public void tokenGeneratedTwiceForIPAddress_shouldReturnSameKey() throws Exception {
            String token = service.generateToken("2.2.2.2");
            assertEquals(token, service.generateToken("2.2.2.2"));
        }

        @Test
        public void tokenGeneratedTwiceForIPAddressAfterTimeout_shouldReturnDifferentKey() throws Exception {
            service = new LocalTokenService(0);
            String token = service.generateToken("2.2.2.2");
            assertNotEquals(token, service.generateToken("2.2.2.2"));
        }

        @Test
        public void tokenGeneratedTwiceForIPAddressAfterTimeout_shouldNotIncreaseTheNumberOfTokens() throws Exception {
            service = new LocalTokenService(0);
            String token = service.generateToken("2.2.2.2");
            int numberOfToken = service.totalNumberTokens();
            service.generateToken("2.2.2.2");
            assertEquals(numberOfToken, service.totalNumberTokens());
        }
    }
}