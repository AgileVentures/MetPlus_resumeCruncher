package org.metplus.curriculum.web.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.metplus.curriculum.database.domain.CruncherSettings;
import org.metplus.curriculum.database.domain.Setting;
import org.metplus.curriculum.database.domain.Settings;
import org.metplus.curriculum.database.exceptions.CruncherSettingsNotFound;
import org.metplus.curriculum.database.repository.SettingsRepository;
import org.metplus.curriculum.security.services.TokenService;
import org.mockito.ArgumentCaptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@WebMvcTest(controllers = AdminController.class)
@AutoConfigureRestDocs("build/generated-snippets")
public class AdminControllerTest extends BaseControllerTest {

    @Autowired
    TokenService tokenService;
    @MockBean
    private SettingsRepository repository;

    @Autowired
    private MockMvc mockMvc;

    private Settings before;

    private String token;

    @Before
    public void setUp() {
        before = new Settings();
        before.addApplicationSetting(new Setting<>("simple test", "Value"));
        ArrayList<Settings> allSettings = new ArrayList<>();
        allSettings.add(before);
        when(repository.findAll()).thenReturn(allSettings);
        token = tokenService.generateToken("0.0.0.0");
    }

    @Test
    public void simpleRetrievalOfSettings() throws Exception {

        ObjectMapper mapper = new ObjectMapper();

        MockHttpServletResponse response = mockMvc.perform(get("/api/v1/admin/settings")
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Auth-Token", token))
                .andDo(document("admin-settings/simple-retrieval-of-settings",
                        responseFields(
                                subsectionWithPath("cruncherSettings").description("All settings from all crunchers"),
                                subsectionWithPath("appSettings").description("Settings of the application"),
                                fieldWithPath("id").description("Identifier").optional())))
                .andExpect(status().isOk())
                .andReturn().getResponse();
        Settings set = mapper.readValue(response.getContentAsByteArray(), Settings.class);

        assertEquals("Value", set.getApplicationSetting("simple test").getData());

    }

    @Test
    public void simpleUpdateSettingsWithNoChanges() throws Exception {

        ObjectMapper mapper = new ObjectMapper();

        MockHttpServletResponse response = mockMvc.perform(get("/api/v1/admin/settings")
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Auth-Token", token))
                .andReturn().getResponse();
        Settings set = mapper.readValue(response.getContentAsByteArray(), Settings.class);
        String strSet = mapper.writeValueAsString(set);
        mockMvc.perform(post("/api/v1/admin/settings")
                .header("X-Auth-Token", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(strSet))
                .andExpect(status().isOk());
    }

    @Test
    public void updateSettingsWithWithChanges() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        before.addApplicationSetting(new Setting<>("shall update", 1));
        String strSet = mapper.writeValueAsString(before);

        mockMvc.perform(post("/api/v1/admin/settings")
                .header("X-Auth-Token", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(strSet))
                .andExpect(status().isOk());

        ArgumentCaptor<Settings> settingsCaptor = ArgumentCaptor.forClass(Settings.class);
        verify(repository).save(settingsCaptor.capture());
        Settings after = settingsCaptor.getValue();
        assertEquals(1, after.getApplicationSetting("shall update").getData());
    }


    @Test
    public void updateSettingsWithWithChangesError() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        before.addCruncherSettings("error not found", new CruncherSettings());
        String strSet = mapper.writeValueAsString(before);

        mockMvc.perform(post("/api/v1/admin/settings")
                .header("X-Auth-Token", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(strSet))
                .andExpect(status().is4xxClientError());

        verify(repository, times(0)).save(any(Settings.class));

    }
}