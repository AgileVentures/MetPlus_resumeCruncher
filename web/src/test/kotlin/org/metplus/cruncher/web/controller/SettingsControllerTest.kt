package org.metplus.cruncher.web.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.assertj.core.api.Assertions
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasEntry
import org.hamcrest.Matchers.hasKey
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.metplus.cruncher.settings.ApplicationSettings
import org.metplus.cruncher.settings.Setting
import org.metplus.cruncher.settings.Settings
import org.metplus.cruncher.settings.SettingsRepository
import org.metplus.cruncher.settings.SettingsRepositoryFake
import org.metplus.cruncher.web.TestConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [TestConfiguration::class, SettingsController::class], inheritLocations = true)
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
@AutoConfigureMockMvc
@WebMvcTest(SettingsController::class)
class SettingsControllerTest(@Autowired private val mvc: MockMvc) {
    @Autowired
    lateinit var settingsRepository: SettingsRepository

    @Test
    @Throws(Exception::class)
    fun `When no settings are present it returns a default settings object`() {
        (settingsRepository as SettingsRepositoryFake).removeAll()
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


    @Test
    @Throws(Exception::class)
    fun `when get and save the same object without changes it returns the correct settings object`() {
        (settingsRepository as SettingsRepositoryFake).removeAll()

        val mapper = ObjectMapper()
        mapper.registerModule(KotlinModule())

        val response = mvc.perform(get("/api/v1/admin/settings/")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andReturn().response
        val settingSaved = mapper.readValue<SettingsController.SettingsResponse>(response.contentAsByteArray)
        val strSet = jacksonObjectMapper().writeValueAsString(settingSaved)

        mvc.perform(post("/api/v1/admin/settings/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(strSet.toString()))
                .andExpect(status().isOk)

        Assertions.assertThat(settingsRepository.getAll().first())
                .isNotNull
                .isEqualToComparingFieldByField(settingSaved.toSettings())
    }

    @Test
    @Throws(Exception::class)
    fun `when update an existing settings it saves to the database the new setting`() {
        (settingsRepository as SettingsRepositoryFake).removeAll()

        val settingsBefore = settingsRepository.save(
                Settings(1, ApplicationSettings(
                        hashMapOf("some content" to Setting("some content", "some value"))
                ))
        )
        val jsonResponseRepresentation = jacksonObjectMapper().writeValueAsString(settingsBefore.toSettingsResponse())

        mvc.perform(post("/api/v1/admin/settings/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonResponseRepresentation))
                .andExpect(status().isOk)

        Assertions.assertThat(settingsRepository.getAll().first())
                .isNotNull
                .isEqualToComparingFieldByField(settingsBefore)

        mvc.perform(get("/api/v1/admin/settings/")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andDo(document("admin-settings/simple-retrieval-of-settings",
                        responseFields(
                                subsectionWithPath("cruncherSettings").description("All settings from all crunchers"),
                                subsectionWithPath("appSettings").description("SettingsController of the application"),
                                fieldWithPath("id").description("Identifier"))))
                .andExpect(jsonPath("$.id", equalTo(1)))
                .andExpect(jsonPath("$.appSettings.settings", hasKey("some content")))
                .andExpect(jsonPath("$.appSettings.settings['some content']", hasEntry("name", "some content")))
                .andExpect(jsonPath("$.appSettings.settings['some content']", hasEntry("data", "some value")))
                .andReturn().response
    }


    @Test
    @Throws(Exception::class)
    fun `when passing a incorrectly structured json object it returns 400`() {
        (settingsRepository as SettingsRepositoryFake).removeAll()

        val strSet = jacksonObjectMapper().writeValueAsString("")

        mvc.perform(post("/api/v1/admin/settings/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(strSet))
                .andExpect(status().is4xxClientError)
    }
}