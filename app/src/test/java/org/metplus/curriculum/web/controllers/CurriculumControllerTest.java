package org.metplus.curriculum.web.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.metplus.curriculum.cruncher.Matcher;
import org.metplus.curriculum.cruncher.MatcherList;
import org.metplus.curriculum.database.domain.DocumentWithMetaData;
import org.metplus.curriculum.database.domain.Job;
import org.metplus.curriculum.database.domain.MetaData;
import org.metplus.curriculum.database.domain.Resume;
import org.metplus.curriculum.database.repository.JobRepository;
import org.metplus.curriculum.database.repository.ResumeRepository;
import org.metplus.curriculum.process.ResumeCruncher;
import org.metplus.curriculum.test.BeforeAfterInterface;
import org.metplus.curriculum.test.BeforeAfterRule;
import org.metplus.curriculum.web.answers.GenericAnswer;
import org.metplus.curriculum.web.answers.ResultCodes;
import org.metplus.curriculum.web.answers.ResumeMatchAnswer;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.RestDocumentation;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.Filter;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * Created by Joao Pereira on 03/11/2015.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({CurriculumControllerTest.UploadEndpoint.class,
                     CurriculumControllerTest.DownloadEndpoint.class,
                     CurriculumControllerTest.MatchEndpoint.class,
                     CurriculumControllerTest.MatchEndpointWithJobId.class})
public class CurriculumControllerTest {
    public static class DefaultCurriculumTest extends BaseControllerTest implements BeforeAfterInterface{
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


        public RestDocumentation restDocumentation;

        public BeforeAfterRule beforeAfter;
        @Rule
        public TestRule chain =
                RuleChain.outerRule(restDocumentation = new RestDocumentation("build/generated-snippets"))
                        .around(beforeAfter = new BeforeAfterRule(this));

        @Override
        public void after() {

        }

        @Override
        public void before() {
            this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                    .addFilter(springSecurityFilterChain)
                    .apply(documentationConfiguration(this.restDocumentation))
                    .alwaysDo(document("curriculum/{method-name}/{step}/",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint())))
                    .build();
            MockHttpServletResponse response = null;
            try {
                response = mockMvc
                        .perform(post("/api/v1/authenticate")
                                .header("X-Auth-Username", backendAdminUsername)
                                .header("X-Auth-Password", backendAdminPassword)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                        ).andExpect(status().isOk()).andReturn().getResponse();
            } catch (Exception e) {
                e.printStackTrace();
            }
            JSONParser parser = new JSONParser();
            JSONObject obj = null;
            try {
                obj = (JSONObject) parser.parse(response.getContentAsString());
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            token = (String) obj.get("token");
        }
        protected LocalValidatorFactoryBean validator() {
            return new LocalValidatorFactoryBean();
        }
    }

    @RunWith(SpringJUnit4ClassRunner.class)
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

    @RunWith(SpringJUnit4ClassRunner.class)
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

    @RunWith(MockitoJUnitRunner.class)
    public static class MatchEndpoint extends DefaultCurriculumTest {
        @Mock
        protected JobRepository jobRepository;
        @Mock
        protected ResumeRepository resumeRepository;
        @Mock
        protected MatcherList matcherList;
        @Mock
        protected ResumeCruncher resumeCruncher;

        @Override
        public void before(){

            mockMvc = MockMvcBuilders.standaloneSetup(new CurriculumController(jobRepository, resumeRepository, matcherList, resumeCruncher))
                    .setValidator(validator())
                    .apply(documentationConfiguration(this.restDocumentation))
                    .build();
            token = "123-1234-1234";
        }
        @Test
        public void noMatches() throws Exception {

            Matcher matcher1 = Mockito.mock(Matcher.class);
            Mockito.when(matcher1.getCruncherName()).thenReturn("matcher1");
            Mockito.when(matcher1.match(Mockito.any(Job.class))).thenReturn(new ArrayList<>());
            Matcher matcher2 = Mockito.mock(Matcher.class);
            Mockito.when(matcher2.getCruncherName()).thenReturn("matcher2");
            Mockito.when(matcher2.match(Mockito.any(Job.class))).thenReturn(new ArrayList<>());

            List<Matcher> allMatchers = new ArrayList<>();
            allMatchers.add(matcher1);
            allMatchers.add(matcher2);
            Mockito.when(matcherList.getMatchers()).thenReturn(allMatchers);

            MockHttpServletResponse response = mockMvc.perform(post("/api/v1/curriculum/match")
                    .header("X-Auth-Token", token)
                    .accept(MediaType.APPLICATION_JSON)
                    .param("title", "Stone mason")
                    .param("description", "Stone mason that is able to build some nice walls"))
                    .andExpect(status().isOk())
                    .andDo(document("curriculum/match-no-resumes",
                            requestHeaders(headerWithName("X-Auth-Token")
                                    .description("Authentication token retrieved from the authentication")),
                            requestParameters(parameterWithName("title")
                                                      .description("Title of the Job"),
                                              parameterWithName("description")
                                                      .description("Description of the Job")),
                            responseFields(
                                    fieldWithPath("resultCode").type(ResultCodes.class).description("Result code"),
                                    fieldWithPath("message").description("Message associated with the result code"),
                                    fieldWithPath("resumes").description("Hash with the identifiers of the resumes matched for each cruncher")
                            )
                    ))
                    .andReturn().getResponse();
            ResumeMatchAnswer answer = new ObjectMapper().readValue(response.getContentAsString(), ResumeMatchAnswer.class);
            assertEquals("Result code is not correct", ResultCodes.SUCCESS, answer.getResultCode());
            assertEquals("Number of resumes should be 0", 0, answer.getResumes().size());
        }
    }
    @RunWith(MockitoJUnitRunner.class)
    public static class MatchEndpointWithJobId extends DefaultCurriculumTest {
        @Mock
        protected JobRepository jobRepository;
        @Mock
        protected ResumeRepository resumeRepository;
        @Mock
        protected MatcherList matcherList;
        @Mock
        protected ResumeCruncher resumeCruncher;

        @Override
        public void before(){

            mockMvc = MockMvcBuilders.standaloneSetup(new CurriculumController(jobRepository, resumeRepository, matcherList, resumeCruncher))
                    .setValidator(validator())
                    .apply(documentationConfiguration(this.restDocumentation))
                    .build();
            token = "123-1234-1234";
        }
        @Test
        public void noMatches() throws Exception {


            Matcher matcher1 = Mockito.mock(Matcher.class);
            Mockito.when(matcher1.getCruncherName()).thenReturn("matcher1");
            Mockito.when(matcher1.match(Mockito.any(Job.class))).thenReturn(new ArrayList<>());
            Matcher matcher2 = Mockito.mock(Matcher.class);
            Mockito.when(matcher2.getCruncherName()).thenReturn("matcher2");
            Mockito.when(matcher2.match(Mockito.any(Job.class))).thenReturn(new ArrayList<>());

            List<Matcher> allMatchers = new ArrayList<>();
            allMatchers.add(matcher1);
            allMatchers.add(matcher2);
            Mockito.when(matcherList.getMatchers()).thenReturn(allMatchers);

            MockHttpServletResponse response = mockMvc.perform(get("/api/v1/curriculum/match/{jobId}", 1)
                    .header("X-Auth-Token", token)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(document("curriculum/match-no-resumes-job-id",
                            requestHeaders(headerWithName("X-Auth-Token")
                                    .description("Authentication token retrieved from the authentication")),
                            pathParameters(
                                    parameterWithName("jobId").description("Job Identifier to retrieve the Resumes that match the job")
                            ),
                            responseFields(
                                    fieldWithPath("resultCode").type(ResultCodes.class).description("Result code"),
                                    fieldWithPath("message").description("Message associated with the result code"),
                                    fieldWithPath("resumes").description("Hash with the identifiers of the resumes matched for each cruncher")
                            )
                    ))
                    .andReturn().getResponse();
            ResumeMatchAnswer answer = new ObjectMapper().readValue(response.getContentAsString(), ResumeMatchAnswer.class);
            assertEquals("Result code is not correct", ResultCodes.SUCCESS, answer.getResultCode());
            assertEquals("Number of resumes should be 0", 0, answer.getResumes().size());
        }
        @Test
        public void multipleMatches() throws Exception {
            DocumentWithMetaData titleMetaData = new DocumentWithMetaData();
            DocumentWithMetaData descriptionMetaData = new DocumentWithMetaData();
            Map<String, MetaData> metaData1 = new HashMap<>();
            metaData1.put("matcher1", new MetaData());
            metaData1.put("matcher2", new MetaData());
            titleMetaData.setMetaData(metaData1);
            descriptionMetaData.setMetaData(metaData1);
            Job job = new Job();
            job.setTitleMetaData(titleMetaData);
            job.setDescriptionMetaData(descriptionMetaData);
            Mockito.when(jobRepository.findByJobId("1")).thenReturn(job);

            Resume resume1 = new Resume("1");
            Resume resume2 = new Resume("2");
            Resume resume3 = new Resume("3");
            List<Resume> matcher1Resumes = new ArrayList<>();
            matcher1Resumes.add(resume1);
            matcher1Resumes.add(resume2);
            List<Resume> matcher2Resumes = new ArrayList<>();
            matcher2Resumes.add(resume3);
            matcher2Resumes.add(resume2);

            Matcher matcher1 = Mockito.mock(Matcher.class);
            Mockito.when(matcher1.getCruncherName()).thenReturn("matcher1");
            Mockito.when(matcher1.match(Mockito.any(Job.class))).thenReturn(matcher1Resumes);
            Matcher matcher2 = Mockito.mock(Matcher.class);
            Mockito.when(matcher2.getCruncherName()).thenReturn("matcher2");
            Mockito.when(matcher2.match(Mockito.any(Job.class))).thenReturn(matcher2Resumes);

            List<Matcher> allMatchers = new ArrayList<>();
            allMatchers.add(matcher1);
            allMatchers.add(matcher2);
            Mockito.when(matcherList.getMatchers()).thenReturn(allMatchers);

            MockHttpServletResponse response = mockMvc.perform(get("/api/v1/curriculum/match/{jobId}", 1)
                    .header("X-Auth-Token", token)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(document("curriculum/multiple-match-resumes-job-id",
                            requestHeaders(headerWithName("X-Auth-Token")
                                    .description("Authentication token retrieved from the authentication")),
                            pathParameters(
                                    parameterWithName("jobId").description("Job Identifier to retrieve the Resumes that match the job")
                            ),
                            responseFields(
                                    fieldWithPath("resultCode").type(ResultCodes.class).description("Result code"),
                                    fieldWithPath("message").description("Message associated with the result code"),
                                    fieldWithPath("resumes").description("Hash with the identifiers of the resumes matched for each cruncher")
                            )
                    ))
                    .andExpect(jsonPath("$.resultCode", is(ResultCodes.SUCCESS.toString())))
                    .andExpect(jsonPath("$.resumes.matcher1", hasSize(2)))
                    .andExpect(jsonPath("$.resumes.matcher1[0]", is("1")))
                    .andExpect(jsonPath("$.resumes.matcher1[1]", is("2")))
                    .andExpect(jsonPath("$.resumes.matcher2", hasSize(2)))
                    .andExpect(jsonPath("$.resumes.matcher2[0]", is("3")))
                    .andExpect(jsonPath("$.resumes.matcher2[1]", is("2")))
                    .andReturn().getResponse();
            Mockito.verify(jobRepository).findByJobId("1");
            Mockito.verify(matcher1).match(Mockito.any(Job.class));
            Mockito.verify(matcher2).match(Mockito.any(Job.class));
        }
    }
}