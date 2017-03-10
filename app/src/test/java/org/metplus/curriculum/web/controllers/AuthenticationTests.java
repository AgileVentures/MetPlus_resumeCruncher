package org.metplus.curriculum.web.controllers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.metplus.curriculum.web.controllers.auth.AuthenticationController;
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

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = AuthenticationController.class)
@AutoConfigureRestDocs("build/generated-snippets")
public class AuthenticationTests extends BaseControllerTest {

    public static final Logger logger = LoggerFactory.getLogger(AdminControllerTest.class);

    @Value("${backend.admin.username}")
    protected String backendAdminUsername;

    @Value("${backend.admin.password}")
    protected String backendAdminPassword;

    @Autowired
    private WebApplicationContext ctx;
    @Autowired
    private MockMvc mockMvc;

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
