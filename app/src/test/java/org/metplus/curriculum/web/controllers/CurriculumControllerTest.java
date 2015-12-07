package org.metplus.curriculum.web.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.ByteStreams;
import org.junit.Before;
import org.junit.Test;
import org.metplus.curriculum.database.domain.Setting;
import org.metplus.curriculum.web.GenericAnswer;
import org.metplus.curriculum.web.ResultCodes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import static org.junit.Assert.*;
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

    @Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx).build();
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
                        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8"))
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
                        get("/api/v1/curriculum/asdasdasd"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/octet-stream"))
                    .andReturn().getResponse();
        assertEquals("The status of not the expected", 200, response.getStatus());

    }

    @Test
    public void testUnableToFindCurriculumDownloadCurriculum() throws Exception {

        MockHttpServletResponse response = mockMvc.perform(get("/api/v1/curriculum/notpresentuser"))
                    .andExpect(status().isOk())
                    .andReturn().getResponse();


        GenericAnswer answer = new ObjectMapper().readValue(response.getContentAsString(), GenericAnswer.class);
        assertEquals("Result code is not correct", ResultCodes.RESUME_NOT_FOUND, answer.getResultCode());
    }
}