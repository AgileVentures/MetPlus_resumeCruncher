package org.metplus.curriculum.web.controllers;

/**
 * Created by joao on 2/18/16.
 */
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthenticationTests extends BaseControllerTest {

    public static final Logger logger = LoggerFactory.getLogger(AdminControllerTest.class);
    @Autowired
    private WebApplicationContext ctx;
    @Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx).build();
    }
    private MockMvc mockMvc;

    @Test
    public void noUserNoPassword() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get("/api/v1/authenticate")
                                    .accept(MediaType.APPLICATION_JSON))
                                    .andExpect(status().is4xxClientError()).andReturn().getResponse();
    }
    @Test
    public void userNoPassword() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get("/api/v1/authenticate")
                                    .accept(MediaType.APPLICATION_JSON)
                                    .header("X-Auth-Username", "backend_admin"))
                                    .andExpect(status().is4xxClientError()).andReturn().getResponse();
    }
    @Test
    public void userPassword() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(post("/api/v1/authenticate")
                                    .accept(MediaType.APPLICATION_JSON)
                                    .header("X-Auth-Username", "backend_admin")
                                    .header("X-Auth-Password", "backendpassword"))
                                    .andExpect(status().is2xxSuccessful())
                                    .andReturn().getResponse();
    }
}
