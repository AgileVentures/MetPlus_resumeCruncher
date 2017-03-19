package org.metplus.curriculum.web.controllers.auth;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.metplus.curriculum.test.BeforeAfterInterface;
import org.metplus.curriculum.test.BeforeAfterRule;
import org.metplus.curriculum.web.controllers.AdminControllerTest;
import org.metplus.curriculum.web.controllers.BaseControllerTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(Suite.class)
@Suite.SuiteClasses({AuthenticationTests.Version1Test.class,
AuthenticationTests.Version2Test.class,
AuthenticationTests.Version99999Test.class})
public class AuthenticationTests extends BaseControllerTest {

    @RunWith(SpringRunner.class)
    @WebMvcTest(controllers = AuthenticationController.class)
    @AutoConfigureRestDocs("build/generated-snippets")
    public static class AuthenticationBaseTests extends BaseControllerTest implements BeforeAfterInterface {
        public static final Logger logger = LoggerFactory.getLogger(AdminControllerTest.class);
        @Rule
        public BeforeAfterRule beforeAfterRule = new BeforeAfterRule(this);
        @Value("${backend.admin.username}")
        protected String backendAdminUsername;
        @Value("${backend.admin.password}")
        protected String backendAdminPassword;
        protected String versionUrl;
        @Autowired
        private WebApplicationContext ctx;
        @Autowired
        private MockMvc mockMvc;

        @Override
        public void after() {

        }

        @Override
        public void before() {

        }

        @Test
        public void noUserNoPassword() throws Exception {
            mockMvc.perform(post("/api/v1/authenticate")
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized())
            ;
        }

        @Test
        public void userNoPassword() throws Exception {
            mockMvc.perform(post("/api/v1/authenticate")
                    .accept(MediaType.APPLICATION_JSON)
                    .header("X-Auth-Username", "backend_admin"))
                    .andExpect(status().isUnauthorized())
            ;
        }

        @Test
        public void userPasswordError() throws Exception {
            mockMvc.perform(post("/api/v1/authenticate")
                    .accept(MediaType.APPLICATION_JSON)
                    .header("X-Auth-Username", "backend_admin1")
                    .header("X-Auth-Password", "backendpassword"))
                    .andExpect(status().isUnauthorized())
                    .andDo(document("authentication/userPassword-error",
                            requestHeaders(headerWithName("X-Auth-Username")
                                            .description("Username user to authenticate the client"),
                                    headerWithName("X-Auth-Password")
                                            .description("Password for the user to authenticate the client"))
                    ));
        }

        @Test
        public void userPassword() throws Exception {
            mockMvc.perform(post("/api/v1/authenticate")
                    .accept(MediaType.APPLICATION_JSON)
                    .header("X-Auth-Username", "backend_admin")
                    .header("X-Auth-Password", "backendpassword"))
                    .andExpect(status().is2xxSuccessful())
                    .andDo(document("authentication/userPassword",
                            requestHeaders(headerWithName("X-Auth-Username")
                                            .description("Username user to authenticate the client"),
                                    headerWithName("X-Auth-Password")
                                            .description("Password for the user to authenticate the client")),
                            responseFields(
                                    fieldWithPath("token").description("Token that should be used in subsequent requests")
                            )
                    ));
        }
    }

    public static class Version1Test extends AuthenticationBaseTests {
        @Override
        public void before() {
            super.before();
            this.versionUrl = "/api/v1/authenticate";
        }
    }

    public static class Version2Test extends AuthenticationBaseTests {
        @Override
        public void before() {
            super.before();
            this.versionUrl = "/api/v2/authenticate";
        }
    }

    public static class Version99999Test extends AuthenticationBaseTests {
        @Override
        public void before() {
            super.before();
            this.versionUrl = "/api/v99999/authenticate";
        }
    }
}
