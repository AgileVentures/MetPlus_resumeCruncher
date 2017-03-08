package org.metplus.curriculum.web.security;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.metplus.curriculum.services.LocalTokenService;
import org.metplus.curriculum.services.TokenService;
import org.metplus.curriculum.test.BeforeAfterInterface;
import org.metplus.curriculum.test.BeforeAfterRule;
import org.metplus.curriculum.useCases.UserTryToLogin;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.Assert.*;

@RunWith(Suite.class)
@Suite.SuiteClasses({ApplicationLoginFilterTest.LoginSuccessfull.class,
        ApplicationLoginFilterTest.LoginError.class})
public class ApplicationLoginFilterTest {
    public static class DefaultLoginTest implements BeforeAfterInterface {
        @Rule
        public BeforeAfterRule beforeAfter = new BeforeAfterRule(this);
        protected UserTryToLogin useCase;
        protected TokenService tokenService;
        protected ApplicationLoginFilter filter;
        protected String validToken;
        @Override
        public void after() {
        }

        @Override
        public void before() {
            useCase = new UserTryToLogin("username", "password");
            tokenService = new LocalTokenService();
            validToken = tokenService.generateToken("1.1.1.1");
            filter = new ApplicationLoginFilter(useCase, tokenService);

        }
    }

    public static class LoginError extends DefaultLoginTest {
        @Test
        public void invalidCredentials_shouldReturnAuthenticationError() throws Exception {
            MockHttpServletRequest request = createRequest("1.1.1.1", "u", "p");
            MockHttpServletResponse response = new MockHttpServletResponse();
            assertFalse(filter.preHandle(request, response, filter));
            assertEquals(401, response.getStatus());
        }

        @Test
        public void noCredentialsPresentCredentials_shouldReturnAuthenticationError() throws Exception {
            MockHttpServletRequest request = new MockHttpServletRequest();
            MockHttpServletResponse response = new MockHttpServletResponse();
            assertFalse(filter.preHandle(request, response, filter));
            assertEquals(401, response.getStatus());
        }
    }

    public static class LoginSuccessfull extends DefaultLoginTest {

        @Test
        public void usernameAndLoginCorrect_shouldReturnGeneratedToken() throws Exception {
            MockHttpServletRequest request = createRequest("1.1.1.1", "username", "password");
            MockHttpServletResponse response = new MockHttpServletResponse();
            assertFalse(filter.preHandle(request, response, filter));
            assertEquals(200, response.getStatus());
            assertEquals("{\"token\": \"" + validToken + "\"}", response.getContentAsString());
            assertEquals("application/json", response.getContentType());
        }
    }

    private static MockHttpServletRequest createRequest(String remoteAddr, String username, String password) {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        request.setRemoteAddr(remoteAddr);
        request.addHeader("X-Auth-Username", username);
        request.addHeader("X-Auth-Password", password);
        return request;
    }
}