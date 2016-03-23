package org.metplus.curriculum.web.controllers;

/**
 * Created by joao on 2/18/16.
 */
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.metplus.curriculum.security.SecurityConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentation;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
public class AuthenticationTests extends BaseControllerTest {

    public static final Logger logger = LoggerFactory.getLogger(AdminControllerTest.class);
    @Autowired
    private WebApplicationContext ctx;

    @Rule
    public RestDocumentation restDocumentation = new RestDocumentation("build/generated-snippets");
    @Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                .apply(documentationConfiguration(this.restDocumentation))
                .build();
    }
    private MockMvc mockMvc;

    @Test
    public void noUserNoPassword() throws Exception {
        mockMvc.perform(get("/api/v1/authenticate")
                                    .accept(MediaType.APPLICATION_JSON))
                                    .andExpect(status().is4xxClientError());
    }
    @Test
    public void userNoPassword() throws Exception {
        mockMvc.perform(get("/api/v1/authenticate")
                                    .accept(MediaType.APPLICATION_JSON)
                                    .header("X-Auth-Username", "backend_admin"))
                                    .andExpect(status().is4xxClientError());
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
                                                    .description("Password for the user to authenticate the client"))
                                            ));
    }
}
