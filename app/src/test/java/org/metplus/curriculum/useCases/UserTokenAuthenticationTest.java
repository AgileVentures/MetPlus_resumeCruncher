package org.metplus.curriculum.useCases;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.metplus.curriculum.services.LocalTokenService;
import org.metplus.curriculum.services.TokenService;
import org.metplus.curriculum.test.BeforeAfterInterface;
import org.metplus.curriculum.test.BeforeAfterRule;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


@RunWith(Suite.class)
@Suite.SuiteClasses({UserTokenAuthenticationTest.AuthenticationSuccessful.class,
        UserTokenAuthenticationTest.AuthenticationFail.class})
public class UserTokenAuthenticationTest {

    public static class DefaultTokenAuthenticationTest implements BeforeAfterInterface {
        @Rule
        public BeforeAfterRule beforeAfter = new BeforeAfterRule(this);
        protected UserTokenAuthentication useCase;
        protected TokenService tokenService;
        protected String[] tokens;

        @Override
        public void after() {
        }

        @Override
        public void before() {
            tokenService = new LocalTokenService(30);
            useCase = new UserTokenAuthentication(tokenService);
            tokens = new String[2];
            tokens[0] = tokenService.generateToken("1.1.1.1");
            tokens[1] = tokenService.generateToken("2.2.2.2");
            System.out.println(tokens[1]);
        }
    }

    public static class AuthenticationSuccessful extends DefaultTokenAuthenticationTest {
        @Test
        public void correctToken_shouldReturnTrue() throws Exception {
            String token = tokens[0];
            assertTrue(useCase.canLogin(token));
        }
    }

    public static class AuthenticationFail extends DefaultTokenAuthenticationTest {
        @Test
        public void incorrectToken_shouldReturnFalse() throws Exception {
            String token = "3dc6cc5a-b4b7-41a0-b9cb-7906c0e2f40d";
            assertFalse(useCase.canLogin(token));
        }
    }
}