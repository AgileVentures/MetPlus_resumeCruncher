package org.metplus.cruncher.web.controller

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.metplus.cruncher.web.TestConfiguration
import org.metplus.cruncher.web.security.SecurityConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Profile
import org.springframework.http.MediaType
import org.springframework.restdocs.headers.HeaderDocumentation.headerWithName
import org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [TestConfiguration::class, SecurityConfig::class, AuthenticationController::class], inheritLocations = true)
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
@AutoConfigureMockMvc
@WebMvcTest(AuthenticationController::class)
class AuthenticationControllerTests(@Autowired private val mvc: MockMvc) {

    @Value("\${backend.admin.username}")
    private var backendAdminUsername: String? = null
    @Value("\${backend.admin.password}")
    private var backendAdminPassword: String? = null

    @ParameterizedTest(name = "{index} => API Version: {0}")
    @ValueSource(strings = ["v1", "v2"])
    fun noUserNoPassword(versionNumber: String) {
        mvc.perform(post("/api/$versionNumber/authenticate")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized)
    }

    @ParameterizedTest(name = "{index} => API Version: {0}")
    @ValueSource(strings = ["v1", "v2"])
    fun userNoPassword(versionNumber: String) {
        mvc.perform(post("/api/$versionNumber/authenticate")
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Auth-Username", "backend_admin"))
                .andExpect(status().isUnauthorized)
    }

    @ParameterizedTest(name = "{index} => API Version: {0}")
    @ValueSource(strings = ["v1", "v2"])
    fun userPasswordError(versionNumber: String) {
        mvc.perform(post("/api/$versionNumber/authenticate")
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Auth-Username", "backend_admin1")
                .header("X-Auth-Password", "backendpassword"))
                .andExpect(status().isUnauthorized)
                .andDo(document("authentication/userPassword-error",
                        requestHeaders(headerWithName("X-Auth-Username")
                                .description("Username user to authenticate the client"),
                                headerWithName("X-Auth-Password")
                                        .description("Password for the user to authenticate the client"))
                ))
    }

    @ParameterizedTest(name = "{index} => API Version: {0}")
    @ValueSource(strings = ["v1", "v2"])
    fun userPassword(versionNumber: String) {
        mvc.perform(post("/api/$versionNumber/authenticate")
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Auth-Username", backendAdminUsername)
                .header("X-Auth-Password", backendAdminPassword))
                .andExpect(status().is2xxSuccessful)
                .andDo(document("authentication/userPassword",
                        requestHeaders(headerWithName("X-Auth-Username")
                                .description("Username user to authenticate the client"),
                                headerWithName("X-Auth-Password")
                                        .description("Password for the user to authenticate the client")),
                        responseFields(
                                fieldWithPath("token").description("Token that should be used in subsequent requests")
                        )
                ))
    }
}
