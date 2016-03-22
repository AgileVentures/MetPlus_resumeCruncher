package org.metplus.curriculum.web.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.metplus.curriculum.web.GenericAnswer;
import org.metplus.curriculum.web.ResultCodes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.RestDocumentation;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.Filter;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * Created by Joao Pereira on 03/11/2015.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({CurriculumControllerTest.UploadEndpoint.class,
                     CurriculumControllerTest.DownloadEndpoint.class,
                     CurriculumControllerTest.MatchEndpoint.class})
public class CurriculumControllerTest {
    public static class DefaultCurriculumTest extends BaseControllerTest {
        @Autowired
        protected WebApplicationContext ctx;

        protected MockMvc mockMvc;
        @Value("${backend.admin.username}")
        protected String backendAdminUsername;

        @Value("${backend.admin.password}")
        protected String backendAdminPassword;

        @Autowired
        private Filter springSecurityFilterChain;
        protected String token;

        @Rule
        public RestDocumentation restDocumentation = new RestDocumentation("build/generated-snippets");

        @Before
        public void setUp() throws Exception {

            /**AuthenticationManager authenticationManager = this.ctx
                    .getBean(AuthenticationManager.class);
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(backendAdminUsername, backendAdminPassword));
            SecurityContextHolder.getContext().setAuthentication(authentication);**/

            this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                    .addFilter(springSecurityFilterChain)
                    .apply(documentationConfiguration(this.restDocumentation))
                    .alwaysDo(document("curriculum/{method-name}/{step}/",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint())))
                    .build();
            MockHttpServletResponse response = mockMvc
                    .perform(post("/api/v1/authenticate")
                                    .header("X-Auth-Username", backendAdminUsername)
                                    .header("X-Auth-Password", backendAdminPassword)
                                    .accept(MediaType.APPLICATION_JSON)
                                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    ).andExpect(status().isOk()).andReturn().getResponse();
            JSONParser parser = new JSONParser();
            JSONObject obj = (JSONObject) parser.parse(response.getContentAsString());
            token = (String) obj.get("token");
        }
    }

    public static class UploadEndpoint extends DefaultCurriculumTest {

        @Test
        public void testUploadCurriculum() throws Exception {
            final InputStream file = getClass().getClassLoader().getResourceAsStream("line_with_bold.pdf");
            final MockMultipartFile multipartFile = new MockMultipartFile("file", file);

            MockHttpServletResponse response = mockMvc
                    .perform(fileUpload("/api/v1/curriculum/upload")
                            .file(multipartFile)
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                            .param("userId", "asdasdasd")
                            .param("name", "line_with_bold.pdf")
                            .header("X-Auth-Token", token)
                    )
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8"))
                    .andDo(document("curriculum/upload",
                            requestHeaders(headerWithName("X-Auth-Token")
                                    .description("Authentication token retrieved from the authentication")),
                            requestParameters(
                                    parameterWithName("userId").description("Identifier of the curriculum"),
                                    parameterWithName("name").description("Name of the curriculum file")
                            ),
                            responseFields(
                                    fieldWithPath("resultCode").type(ResultCodes.class).description("Result code"),
                                    fieldWithPath("message").description("Message associated with the result code")
                            )
                    ))
                    .andReturn()
                    .getResponse();

            assertEquals("The status of not the expected", 200, response.getStatus());

            GenericAnswer answer = new ObjectMapper().readValue(response.getContentAsString(), GenericAnswer.class);
            assertEquals("Result code is not correct", ResultCodes.SUCCESS, answer.getResultCode());
        }

    }
    public static class DownloadEndpoint extends DefaultCurriculumTest {

        @Test
        public void testDownloadCurriculum() throws Exception {
            final InputStream file = getClass().getClassLoader().getResourceAsStream("line_with_bold.pdf");
            final MockMultipartFile multipartFile = new MockMultipartFile("file", file);

            MockHttpServletResponse response = mockMvc.perform(
                    get("/api/v1/curriculum/asdasdasd")
                            .header("X-Auth-Token", token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/octet-stream"))
                    .andDo(document("curriculum/download",
                            requestHeaders(headerWithName("X-Auth-Token")
                                    .description("Authentication token retrieved from the authentication"))
                    ))
                    .andReturn().getResponse();
            assertEquals("The status of not the expected", 200, response.getStatus());

        }

        @Test
        public void testUnableToFindCurriculumDownloadCurriculum() throws Exception {

            MockHttpServletResponse response = mockMvc.perform(get("/api/v1/curriculum/notpresentuser")
                    .header("X-Auth-Token", token))
                    .andExpect(status().isOk())
                    .andDo(document("curriculum/download-error",
                            requestHeaders(headerWithName("X-Auth-Token")
                                    .description("Authentication token retrieved from the authentication")),
                            responseFields(
                                    fieldWithPath("resultCode").type(ResultCodes.class).description("Result code"),
                                    fieldWithPath("message").description("Message associated with the result code")
                            )
                    ))
                    .andReturn().getResponse();


            GenericAnswer answer = new ObjectMapper().readValue(response.getContentAsString(), GenericAnswer.class);
            assertEquals("Result code is not correct", ResultCodes.RESUME_NOT_FOUND, answer.getResultCode());
        }
    }

    public static class MatchEndpoint extends DefaultCurriculumTest {
        @Test
        public void noMatches() throws Exception {
            MockHttpServletResponse response = mockMvc.perform(post("/api/v1/curriculum/match")
                    .header("X-Auth-Token", token)
                    .accept(MediaType.APPLICATION_JSON)
                    .param("title", "My title")
                    .param("description", "My description"))
                    .andExpect(status().isOk())
                    .andDo(document("curriculum/match-error",
                            requestHeaders(headerWithName("X-Auth-Token")
                                    .description("Authentication token retrieved from the authentication")),
                            requestFields(fieldWithPath("title")
                                            .description("Title of the Job"),
                                    fieldWithPath("description")
                                            .description("Description of the Job")),
                            responseFields(
                                    fieldWithPath("resultCode").type(ResultCodes.class).description("Result code"),
                                    fieldWithPath("message").description("Message associated with the result code"),
                                    fieldWithPath("resumes").description("Hash with the resumes found for each cruncher")
                            )
                    ))
                    .andReturn().getResponse();
            MatchAnswer answer = new ObjectMapper().readValue(response.getContentAsString(), MatchAnswer.class);
            assertEquals("Result code is not correct", ResultCodes.SUCCESS, answer.getResultCode());
            assertEquals("Number of resumes should be 0", 0, answer.getResumes().size());
        }
    }
}