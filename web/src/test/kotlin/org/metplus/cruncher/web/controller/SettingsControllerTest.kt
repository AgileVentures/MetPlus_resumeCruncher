package org.metplus.cruncher.web.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.metplus.cruncher.settings.GetSettings
import org.metplus.cruncher.settings.Settings
import org.metplus.cruncher.settings.SettingsRepository
import org.metplus.cruncher.web.TestConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MockMvcBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup
import org.springframework.web.context.WebApplicationContext


@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [TestConfiguration::class, SettingsController::class], inheritLocations = true)
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
@AutoConfigureMockMvc
@WebMvcTest(SettingsController::class)
class SettingsControllerTest(@Autowired private val mvc: MockMvc) {
    @Autowired
    lateinit var settingsRepository: SettingsRepository
    @Autowired
    lateinit var getSettings: GetSettings

    @Test
    @Throws(Exception::class)
    fun shouldReturnDefaultMessage() {
        mvc.perform(get("/api/v1/admin/settings/")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andDo(document("admin-settings/simple-retrieval-of-settings",
                        responseFields(
                                subsectionWithPath("cruncherSettings").description("All settings from all crunchers"),
                                subsectionWithPath("appSettings").description("SettingsController of the application"),
                                fieldWithPath("id").description("Identifier"))))
                .andExpect(jsonPath("$.id", equalTo(1)))
                .andReturn().response
    }


//    @Test
//    @Throws(Exception::class)
//    fun simpleUpdateSettingsWithNoChanges() {
//
//        val mapper = ObjectMapper()
//        mapper.registerModule(KotlinModule())
//
//        val response = mvc.perform(get("/api/v1/admin/settings/")
//                .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk)
//                .andReturn().response
//        val set = mapper.readValue<SettingsController.SettingsResponse>(response.contentAsByteArray)
//        val strSet = mapper.writeValueAsString(set)
//        mvc.perform(post("/api/v1/admin/settings")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(strSet))
//                .andExpect(status().isOk)
//    }

//    @Test
//    @Throws(Exception::class)
//    fun updateSettingsWithWithChanges() {
//
//        val mapper = ObjectMapper()
//        val settingsBefore = settingsRepository.save(Settings(1, ApplicationSettings()))
//
//
//        val strSet = mapper.writeValueAsString(settingsBefore)
//
//        mvc.perform(post("/api/v1/admin/settings")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(strSet))
//                .andExpect(status().isOk)
//
//        val settingsCaptor = ArgumentCaptor.forClass(Settings::class.java)
//        verify<Any>(repository).save(settingsCaptor.capture())
//        val after = settingsCaptor.getValue()
//        assertEquals(1, after.getApplicationSetting("shall update").getData())
//    }
//
//
//    @Test
//    @Throws(Exception::class)
//    fun updateSettingsWithWithChangesError() {
//        val mapper = ObjectMapper()
//
//        before.addCruncherSettings("error not found", CruncherSettings())
//        val strSet = mapper.writeValueAsString(before)
//
//        mvc.perform(post("/api/v1/admin/settings")
//                .header("X-Auth-Token", token)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(strSet))
//                .andExpect(status().is4xxClientError)
//
//        verify<Any>(repository, times(0)).save(ArgumentMatchers.any(Settings::class.java))
//
//    }
}