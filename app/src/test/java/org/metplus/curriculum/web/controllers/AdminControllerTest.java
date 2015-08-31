package org.metplus.curriculum.web.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.metplus.curriculum.config.DatabaseConfig;
import org.metplus.curriculum.database.config.SpringMongoConfig;
import org.metplus.curriculum.database.domain.CruncherSettings;
import org.metplus.curriculum.database.domain.Setting;
import org.metplus.curriculum.database.domain.Settings;
import org.metplus.curriculum.database.exceptions.CruncherSettingsNotFound;
import org.metplus.curriculum.database.repository.SettingsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

/**
 * Created by Joao Pereira on 31/08/2015.
 */
@ActiveProfiles("development")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SpringMongoConfig.class, DatabaseConfig.class, AdminController.class})
@WebAppConfiguration
public class AdminControllerTest {

    public static final Logger logger = LoggerFactory.getLogger(AdminControllerTest.class);
    @Autowired private SettingsRepository repository;

    @Autowired
    private WebApplicationContext ctx;

    private Settings before;
    @Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx).build();
        before = repository.findAll().iterator().next();
        before.addApplicationSetting(new Setting<>("simple test", "Value"));
        repository.save(before);
    }
    private MockMvc mockMvc;

    @Test
    public void simpleRetrievalOfSettings() throws Exception {

        ObjectMapper mapper = new ObjectMapper();

        MockHttpServletResponse response = mockMvc.perform(get("/admin/settings").accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();
        Settings set = mapper.readValue(response.getContentAsByteArray(), Settings.class);

        assertEquals("Value", set.getApplicationSetting("simple test").getData());

    }

    @Test
    public void simpleUpdateSettingsWithNoChanges() throws Exception {

        ObjectMapper mapper = new ObjectMapper();

        MockHttpServletResponse response = mockMvc.perform(get("/admin/settings").accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();
        Settings set = mapper.readValue(response.getContentAsByteArray(), Settings.class);
        String strSet = mapper.writeValueAsString(set);
        mockMvc.perform(post("/admin/settings").contentType(MediaType.APPLICATION_JSON)
                        .content(strSet))
                .andExpect(status().isOk());


    }

    @Test
    public void updateSettingsWithWithChanges() throws Exception {

        ObjectMapper mapper = new ObjectMapper();

        MockHttpServletResponse response = mockMvc.perform(get("/admin/settings").accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();
        Settings set = mapper.readValue(response.getContentAsByteArray(), Settings.class);
        set.addApplicationSetting(new Setting<>("shall update", 1));
        String strSet = mapper.writeValueAsString(set);
        mockMvc.perform(post("/admin/settings").contentType(MediaType.APPLICATION_JSON)
                .content(strSet))
                .andExpect(status().isOk());

        Settings after = repository.findAll().iterator().next();
        assertEquals(1, after.getApplicationSetting("shall update").getData());

    }


    @Test
    public void updateSettingsWithWithChangesError() throws Exception {

        ObjectMapper mapper = new ObjectMapper();

        MockHttpServletResponse response = mockMvc.perform(get("/admin/settings").accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();
        Settings set = mapper.readValue(response.getContentAsByteArray(), Settings.class);
        set.addCruncherSettings("error not found", new CruncherSettings());
        String strSet = mapper.writeValueAsString(set);

        mockMvc.perform(post("/admin/settings").contentType(MediaType.APPLICATION_JSON)
                .content(strSet))
                .andExpect(status().is4xxClientError());

        Settings after = repository.findAll().iterator().next();
        try {
            after.getCruncherSettings("error not found");
        } catch(CruncherSettingsNotFound e) {
            return;
        }
        throw new Exception("The cruncher settings should not exist");

    }
}