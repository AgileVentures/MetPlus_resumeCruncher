package org.metplus.curriculum.security.services;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.metplus.curriculum.test.BeforeAfterInterface;
import org.metplus.curriculum.test.BeforeAfterRule;

import static org.junit.Assert.*;


@RunWith(Suite.class)
@Suite.SuiteClasses({LocalTokenServiceTest.ValidToken.class,
        LocalTokenServiceTest.GenerateToken.class})
public class LocalTokenServiceTest {

    public static class DefaultLocalTokenServiceTest implements BeforeAfterInterface {
        @Rule
        public BeforeAfterRule beforeAfter = new BeforeAfterRule(this);
        protected LocalTokenService service;
        protected String generatedKey;

        @Override
        public void after() {
        }

        @Override
        public void before() {
            service = new LocalTokenService();
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