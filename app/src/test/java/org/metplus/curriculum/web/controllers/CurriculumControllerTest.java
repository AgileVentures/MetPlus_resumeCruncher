package org.metplus.curriculum.web.controllers;

import com.google.common.io.ByteStreams;
import org.junit.Before;
import org.junit.Test;
import org.metplus.curriculum.database.domain.Setting;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

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
        final MockMultipartFile multipartFile = new MockMultipartFile("aMultiPartFile.txt", file);

        MockHttpServletResponse response = mockMvc
                .perform(post("/api/v1/curriculum/upload")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .requestAttr("userId", "asdasdasd")
                        .requestAttr("name", "line_with_bold.pdf")
                        .requestAttr("file", multipartFile.getBytes()))
                .andReturn()
                .getResponse();
        System.out.println("bamm:" + response.getStatus());
        System.out.println("bamm:" + response.getContentAsString());
        System.out.println();
    }
}