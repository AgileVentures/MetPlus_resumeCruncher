package org.metplus.curriculum.web.security;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.metplus.curriculum.services.LocalTokenService;
import org.metplus.curriculum.services.TokenService;
import org.metplus.curriculum.test.BeforeAfterInterface;
import org.metplus.curriculum.test.BeforeAfterRule;
import org.metplus.curriculum.useCases.UserTokenAuthentication;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.Assert.*;


@RunWith(Suite.class)
@Suite.SuiteClasses({ApplicationTokenAuthenticationFilterTest.LoginSuccessfull.class,
        ApplicationTokenAuthenticationFilterTest.LoginError.class})
public class ApplicationTokenAuthenticationFilterTest {
    public static class DefaultLoginTest implements BeforeAfterInterface {
        @Rule
        public BeforeAfterRule beforeAfter = new BeforeAfterRule(this);
        protected UserTokenAuthentication useCase;
        protected TokenService tokenService;
        protected ApplicationTokenAuthenticationFilter filter;
        protected String validToken;
        @Override
        public void after() {
        }

        @Override
        public void before() {
            tokenService = new LocalTokenService();
            useCase = new UserTokenAuthentication(tokenService);
            validToken = tokenService.generateToken("1.1.1.1");
            filter = new ApplicationTokenAuthenticationFilter(tokenService);
        }
    }
    public static class LoginError extends DefaultLoginTest {
        @Test
        public void invalidToken_shouldReturnAuthenticationError() throws Exception {
            MockHttpServletRequest request = createRequest("1.1.1.1", "fe02176d-4c64-4404-b734-94232a4d54cc");
            MockHttpServletResponse response = new MockHttpServletResponse();
            assertFalse(filter.preHandle(request, response, filter));
            assertEquals(401, response.getStatus());
        }

        @Test
        public void noTokenPresent_shouldReturnAuthenticationError() throws Exception {
            MockHttpServletRequest request = new MockHttpServletRequest();
            MockHttpServletResponse response = new MockHttpServletResponse();
            assertFalse(filter.preHandle(request, response, filter));
            assertEquals(401, response.getStatus());
        }
    }

    public static class LoginSuccessfull extends DefaultLoginTest {

        @Test
        public void tokenCorrect_shouldReturnGeneratedToken() throws Exception {
            MockHttpServletRequest request = createRequest("1.1.1.1", validToken);
            MockHttpServletResponse response = new MockHttpServletResponse();
            assertTrue(filter.preHandle(request, response, filter));
        }
    }

    private static MockHttpServletRequest createRequest(String remoteAddr, String token) {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        request.setRemoteAddr(remoteAddr);
        request.addHeader("X-Auth-Token", token);
        return request;
    }
}