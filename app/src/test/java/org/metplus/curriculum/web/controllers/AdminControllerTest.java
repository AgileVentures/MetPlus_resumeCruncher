package org.metplus.curriculum.web.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.metplus.curriculum.database.domain.CruncherSettings;
import org.metplus.curriculum.database.domain.Setting;
import org.metplus.curriculum.database.domain.Settings;
import org.metplus.curriculum.database.exceptions.CruncherSettingsNotFound;
import org.metplus.curriculum.database.repository.SettingsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.restdocs.RestDocumentation;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.NoSuchElementException;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;

import static org.junit.Assert.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@RunWith(SpringJUnit4ClassRunner.class)
public class AdminControllerTest  extends BaseControllerTest {

    public static final Logger logger = LoggerFactory.getLogger(AdminControllerTest.class);
    @Autowired private SettingsRepository repository;

    @Autowired
    private WebApplicationContext ctx;

    private Settings before;
    @Rule
    public RestDocumentation restDocumentation = new RestDocumentation("build/generated-snippets");

    @Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                .apply(documentationConfiguration(this.restDocumentation))
                .alwaysDo(document("{method-name}/{step}/",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())))
                .build();
        try {
            before = repository.findAll().iterator().next();
        } catch(NoSuchElementException exp) {
            before = new Settings();
        }
        before.addApplicationSetting(new Setting<>("simple test", "Value"));
        repository.save(before);
    }
    private MockMvc mockMvc;

    @Test
    public void simpleRetrievalOfSettings() throws Exception {

        ObjectMapper mapper = new ObjectMapper();

        MockHttpServletResponse response = mockMvc.perform(get("/api/v1/admin/settings").accept(MediaType.APPLICATION_JSON))
                .andDo(document("admin-settings",
                        responseFields(
                                fieldWithPath("cruncherSettings").description("All settings from all crunchers"),
                                fieldWithPath("appSettings").description("Settings of the application"),
                                fieldWithPath("id").description("Identifier"))))
                .andReturn().getResponse();
        Settings set = mapper.readValue(response.getContentAsByteArray(), Settings.class);

        assertEquals("Value", set.getApplicationSetting("simple test").getData());

    }

    @Test
    public void simpleUpdateSettingsWithNoChanges() throws Exception {

        ObjectMapper mapper = new ObjectMapper();

        MockHttpServletResponse response = mockMvc.perform(get("/api/v1/admin/settings").accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();
        Settings set = mapper.readValue(response.getContentAsByteArray(), Settings.class);
        String strSet = mapper.writeValueAsString(set);
        mockMvc.perform(post("/api/v1/admin/settings").contentType(MediaType.APPLICATION_JSON)
                        .content(strSet))
                .andExpect(status().isOk());
    }

    @Test
    public void updateSettingsWithWithChanges() throws Exception {

        ObjectMapper mapper = new ObjectMapper();

        MockHttpServletResponse response = mockMvc.perform(get("/api/v1/admin/settings").accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();
        Settings set = mapper.readValue(response.getContentAsByteArray(), Settings.class);
        set.addApplicationSetting(new Setting<>("shall update", 1));
        String strSet = mapper.writeValueAsString(set);
        mockMvc.perform(post("/api/v1/admin/settings").contentType(MediaType.APPLICATION_JSON)
                .content(strSet))
                .andExpect(status().isOk());

        Settings after = repository.findAll().iterator().next();
        assertEquals(1, after.getApplicationSetting("shall update").getData());

    }


    @Test
    public void updateSettingsWithWithChangesError() throws Exception {

        ObjectMapper mapper = new ObjectMapper();

        MockHttpServletResponse response = mockMvc.perform(get("/api/v1/admin/settings").accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();
        Settings set = mapper.readValue(response.getContentAsByteArray(), Settings.class);
        set.addCruncherSettings("error not found", new CruncherSettings());
        String strSet = mapper.writeValueAsString(set);

        mockMvc.perform(post("/api/v1/admin/settings").contentType(MediaType.APPLICATION_JSON)
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