package org.metplus.curriculum.web.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.ByteStreams;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.metplus.curriculum.database.domain.Setting;
import org.metplus.curriculum.web.GenericAnswer;
import org.metplus.curriculum.web.ResultCodes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.RestDocumentation;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import static org.junit.Assert.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Joao Pereira on 03/11/2015.
 */
public class CurriculumControllerTest extends BaseControllerTest{

    @Autowired
    private WebApplicationContext ctx;

    private MockMvc mockMvc;
    @Rule
    public RestDocumentation restDocumentation = new RestDocumentation("build/generated-snippets");

    @Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                .apply(documentationConfiguration(this.restDocumentation))
                .alwaysDo(document("curriculum/{method-name}/{step}/",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())))
                .build();
    }
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
                        .header("X-Auth-Token", "123123123")
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
    @Test
    public void testDownloadCurriculum() throws Exception {
        final InputStream file = getClass().getClassLoader().getResourceAsStream("line_with_bold.pdf");
        final MockMultipartFile multipartFile = new MockMultipartFile("file", file);

        MockHttpServletResponse response = mockMvc.perform(
                        get("/api/v1/curriculum/asdasdasd")
                            .header("X-Auth-Token", "123123123"))
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
                            .header("X-Auth-Token", "123123123"))
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