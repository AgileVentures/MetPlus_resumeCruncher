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
import org.metplus.curriculum.api.WebMvcConfig;
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
import org.metplus.curriculum.test.MyStandaloneBuilder;
import org.metplus.curriculum.web.answers.GenericAnswer;
import org.metplus.curriculum.web.answers.ResultCodes;
import org.metplus.curriculum.web.answers.ResumeMatchAnswer;
import org.metplus.curriculum.web.answers.StarAnswer;
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
import java.io.ByteArrayOutputStream;
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
@Suite.SuiteClasses({ResumeControllerTest.UploadEndpoint.class,
                     ResumeControllerTest.DownloadEndpoint.class,
                     ResumeControllerTest.MatchEndpointv1.class,
                     ResumeControllerTest.MatchEndpointv2.class,
                     ResumeControllerTest.MatchEndpointWithJobIdv1.class,
                     ResumeControllerTest.MatchEndpointWithJobIdv2.class,
                     ResumeControllerTest.CompareEndpointv2.class})
public class ResumeControllerTest {
    public static class DefaultResumeTest extends BaseControllerTest implements BeforeAfterInterface{
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

        @Mock
        protected JobRepository jobRepository;
        @Mock
        protected ResumeRepository resumeRepository;
        @Mock
        protected MatcherList matcherList;
        @Mock
        protected ResumeCruncher resumeCruncher;

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
            mockMvc = new MyStandaloneBuilder(new ResumeController(jobRepository, resumeRepository, matcherList, resumeCruncher), new WebMvcConfig())
                    .setValidator(validator())
                    .apply(documentationConfiguration(this.restDocumentation))
                    .alwaysDo(document("resume/{method-name}/{step}/",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint())))
                    .build();
            token = "123-1234-1234";
        }
        protected LocalValidatorFactoryBean validator() {
            return new LocalValidatorFactoryBean();
        }
    }

    @RunWith(MockitoJUnitRunner.class)
    public static class UploadEndpoint extends DefaultResumeTest {

        @Test
        public void testUploadresume() throws Exception {
            final InputStream file = getClass().getClassLoader().getResourceAsStream("line_with_bold.pdf");
            final MockMultipartFile multipartFile = new MockMultipartFile("file", file);
            Resume resume = Mockito.mock(Resume.class);
            Mockito.when(resumeRepository.findByUserId("asdasdasd")).thenReturn(resume);

            MockHttpServletResponse response = mockMvc
                    .perform(fileUpload("/api/v1/resume/upload")
                            .file(multipartFile)
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                            .param("userId", "asdasdasd")
                            .param("name", "line_with_bold.pdf")
                            .header("X-Auth-Token", token)
                    )
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8"))
                    .andDo(document("resume/upload",
                            requestHeaders(headerWithName("X-Auth-Token")
                                    .description("Authentication token retrieved from the authentication")),
                            requestParameters(
                                    parameterWithName("userId").description("Identifier of the resume"),
                                    parameterWithName("name").description("Name of the resume file")
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

    @RunWith(MockitoJUnitRunner.class)
    public static class DownloadEndpoint extends DefaultResumeTest {

        @Test
        public void testDownloadresume() throws Exception {
            final InputStream file = getClass().getClassLoader().getResourceAsStream("line_with_bold.pdf");
            final MockMultipartFile multipartFile = new MockMultipartFile("file", file);
            Resume resume = Mockito.mock(Resume.class);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Mockito.when(resume.getResume(Mockito.any())).thenReturn(out);

            Mockito.when(resumeRepository.findByUserId("asdasdasd")).thenReturn(resume);

            MockHttpServletResponse response = mockMvc.perform(
                    get("/api/v1/resume/asdasdasd")
                            .header("X-Auth-Token", token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/octet-stream"))
                    .andDo(document("resume/download",
                            requestHeaders(headerWithName("X-Auth-Token")
                                    .description("Authentication token retrieved from the authentication"))
                    ))
                    .andReturn().getResponse();
            assertEquals("The status of not the expected", 200, response.getStatus());

        }

        @Test
        public void testUnableToFindresumeDownloadresume() throws Exception {

            MockHttpServletResponse response = mockMvc.perform(get("/api/v1/resume/notpresentuser")
                    .header("X-Auth-Token", token))
                    .andExpect(status().isOk())
                    .andDo(document("resume/download-error",
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
    public static class MatchEndpointv1 extends DefaultResumeTest {

        @Override
        public void before(){
            mockMvc = new MyStandaloneBuilder(new ResumeController(jobRepository, resumeRepository, matcherList, resumeCruncher), new WebMvcConfig())
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

            MockHttpServletResponse response = mockMvc.perform(post("/api/v1/resume/match")
                    .header("X-Auth-Token", token)
                    .accept(MediaType.APPLICATION_JSON)
                    .param("title", "Stone mason")
                    .param("description", "Stone mason that is able to build some nice walls"))
                    .andExpect(status().isOk())
                    .andDo(document("resume/match-no-resumes/v1",
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
    public static class MatchEndpointv2 extends DefaultResumeTest {

        @Override
        public void before(){
            mockMvc = new MyStandaloneBuilder(new ResumeController(jobRepository, resumeRepository, matcherList, resumeCruncher), new WebMvcConfig())
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

            MockHttpServletResponse response = mockMvc.perform(post("/api/v2/resume/match")
                    .header("X-Auth-Token", token)
                    .accept(MediaType.APPLICATION_JSON)
                    .param("title", "Stone mason")
                    .param("description", "Stone mason that is able to build some nice walls"))
                    .andExpect(status().isOk())
                    .andDo(document("resume/match-no-resumes/v2",
                            requestHeaders(headerWithName("X-Auth-Token")
                                    .description("Authentication token retrieved from the authentication")),
                            requestParameters(parameterWithName("title")
                                            .description("Title of the Job"),
                                    parameterWithName("description")
                                            .description("Description of the Job")),
                            responseFields(
                                    fieldWithPath("resultCode").type(ResultCodes.class).description("Result code"),
                                    fieldWithPath("message").description("Message associated with the result code"),
                                    fieldWithPath("resumes").description("Hash with the identifiers of the resumes and start rating matched for each cruncher")
                            )
                    ))
                    .andReturn().getResponse();
            ResumeMatchAnswer answer = new ObjectMapper().readValue(response.getContentAsString(), ResumeMatchAnswer.class);
            assertEquals("Result code is not correct", ResultCodes.SUCCESS, answer.getResultCode());
            assertEquals("Number of resumes should be 0", 0, answer.getResumes().size());
        }
    }

    @RunWith(MockitoJUnitRunner.class)
    public static class MatchEndpointWithJobIdv1 extends DefaultResumeTest {
        @Override
        public void before(){
            mockMvc = new MyStandaloneBuilder(new ResumeController(jobRepository, resumeRepository, matcherList, resumeCruncher), new WebMvcConfig())
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

            MockHttpServletResponse response = mockMvc.perform(get("/api/v1/resume/match/{jobId}", 1)
                    .header("X-Auth-Token", token)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(document("resume/match-no-resumes-job-id/v1",
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
            resume1.setStarRating(4.2);
            Resume resume2 = new Resume("2");
            resume2.setStarRating(3.2);
            Resume resume3 = new Resume("3");
            resume3.setStarRating(5.);
            Resume resume4 = new Resume("2");
            resume4.setStarRating(1.25);
            List<Resume> matcher1Resumes = new ArrayList<>();
            matcher1Resumes.add(resume1);
            matcher1Resumes.add(resume2);
            List<Resume> matcher2Resumes = new ArrayList<>();
            matcher2Resumes.add(resume3);
            matcher2Resumes.add(resume4);

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

            MockHttpServletResponse response = mockMvc.perform(get("/api/v1/resume/match/{jobId}", 1)
                    .header("X-Auth-Token", token)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(document("resume/multiple-match-resumes-job-id/v1",
                            requestHeaders(headerWithName("X-Auth-Token")
                                    .description("Authentication token retrieved from the authentication")),
                            pathParameters(
                                    parameterWithName("jobId").description("Job Identifier to retrieve the Resumes that match the job")
                            ),
                            responseFields(
                                    fieldWithPath("resultCode").type(ResultCodes.class).description("Result code"),
                                    fieldWithPath("message").description("Message associated with the result code"),
                                    fieldWithPath("resumes").description("List with the identifiers of the resumes matched for each cruncher")
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
    @RunWith(MockitoJUnitRunner.class)
    public static class MatchEndpointWithJobIdv2 extends DefaultResumeTest {
        @Override
        public void before(){
            mockMvc = new MyStandaloneBuilder(new ResumeController(jobRepository, resumeRepository, matcherList, resumeCruncher), new WebMvcConfig())
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

            MockHttpServletResponse response = mockMvc.perform(get("/api/v2/resume/match/{jobId}", 1)
                    .header("X-Auth-Token", token)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(document("resume/match-no-resumes-job-id/v2",
                            requestHeaders(headerWithName("X-Auth-Token")
                                    .description("Authentication token retrieved from the authentication")),
                            pathParameters(
                                    parameterWithName("jobId").description("Job Identifier to retrieve the Resumes that match the job")
                            ),
                            responseFields(
                                    fieldWithPath("resultCode").type(ResultCodes.class).description("Result code"),
                                    fieldWithPath("message").description("Message associated with the result code"),
                                    fieldWithPath("resumes").description("Empty Hash")
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
            resume1.setStarRating(4.2);
            Resume resume2 = new Resume("2");
            resume2.setStarRating(3.26);
            Resume resume3 = new Resume("3");
            resume3.setStarRating(5.);
            Resume resume4 = new Resume("2");
            resume4.setStarRating(1.25);
            List<Resume> matcher1Resumes = new ArrayList<>();
            matcher1Resumes.add(resume1);
            matcher1Resumes.add(resume2);
            List<Resume> matcher2Resumes = new ArrayList<>();
            matcher2Resumes.add(resume3);
            matcher2Resumes.add(resume4);

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

            MockHttpServletResponse response = mockMvc.perform(get("/api/v2/resume/match/{jobId}", 1)
                    .header("X-Auth-Token", token)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(document("resume/multiple-match-resumes-job-id/v2",
                            requestHeaders(headerWithName("X-Auth-Token")
                                    .description("Authentication token retrieved from the authentication")),
                            pathParameters(
                                    parameterWithName("jobId").description("Job Identifier to retrieve the Resumes that match the job")
                            ),
                            responseFields(
                                    fieldWithPath("resultCode").type(ResultCodes.class).description("Result code"),
                                    fieldWithPath("message").description("Message associated with the result code"),
                                    fieldWithPath("resumes").description("Hash with the identifiers of the resumes and the star rating matched for each cruncher")
                            )
                    ))
                    .andExpect(jsonPath("$.resultCode", is(ResultCodes.SUCCESS.toString())))
                    .andExpect(jsonPath("$.resumes.matcher1", hasSize(2)))
                    .andExpect(jsonPath("$.resumes.matcher1[0].resumeId", is("1")))
                    .andExpect(jsonPath("$.resumes.matcher1[0].stars", is(4.2)))
                    .andExpect(jsonPath("$.resumes.matcher1[1].resumeId", is("2")))
                    .andExpect(jsonPath("$.resumes.matcher1[1].stars", is(3.2)))
                    .andExpect(jsonPath("$.resumes.matcher2", hasSize(2)))
                    .andExpect(jsonPath("$.resumes.matcher2[0].resumeId", is("3")))
                    .andExpect(jsonPath("$.resumes.matcher2[0].stars", is(5.)))
                    .andExpect(jsonPath("$.resumes.matcher2[1].resumeId", is("2")))
                    .andExpect(jsonPath("$.resumes.matcher2[1].stars", is(1.2)))
                    .andReturn().getResponse();
            Mockito.verify(jobRepository).findByJobId("1");
            Mockito.verify(matcher1).match(Mockito.any(Job.class));
            Mockito.verify(matcher2).match(Mockito.any(Job.class));
        }
    }
    @RunWith(MockitoJUnitRunner.class)
    public static class CompareEndpointv2 extends DefaultResumeTest {
        @Override
        public void before(){
            mockMvc = new MyStandaloneBuilder(new ResumeController(jobRepository, resumeRepository, matcherList, resumeCruncher), new WebMvcConfig())
                    .setValidator(validator())
                    .apply(documentationConfiguration(this.restDocumentation))
                    .build();
            token = "123-1234-1234";
        }
        @Test
        public void cannotFindJob() throws Exception {


            Mockito.when(jobRepository.findByJobId("13")).thenReturn(null);

            MockHttpServletResponse response = mockMvc.perform(get("/api/v2/resume/{resumeId}/compare/{jobId}", "12", "13")
                    .header("X-Auth-Token", token)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andDo(document("resume/compare-job-not-found",
                            requestHeaders(headerWithName("X-Auth-Token")
                                    .description("Authentication token retrieved from the authentication")),
                            pathParameters(
                                    parameterWithName("resumeId").description("Resume Identifier to compare against the Job"),
                                    parameterWithName("jobId").description("Job Identifier compare against the Resume")
                            ),
                            responseFields(
                                    fieldWithPath("resultCode").type(ResultCodes.class).description("Result code"),
                                    fieldWithPath("message").description("Message associated with the result code")
                            )
                    ))
                    .andReturn().getResponse();
            StarAnswer answer = new ObjectMapper().readValue(response.getContentAsString(), StarAnswer.class);
            assertEquals("Result code is not correct", ResultCodes.JOB_NOT_FOUND, answer.getResultCode());
            assertEquals("Number of resumes should be 0", 0, answer.getStars().size());
        }

        @Test
        public void cannotFindResume() throws Exception {


            Mockito.when(jobRepository.findByJobId("13")).thenReturn(new Job());
            Mockito.when(resumeRepository.findByUserId("12")).thenReturn(null);

            MockHttpServletResponse response = mockMvc.perform(get("/api/v2/resume/{resumeId}/compare/{jobId}", "12", "13")
                    .header("X-Auth-Token", token)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andDo(document("resume/compare-resume-not-found",
                            requestHeaders(headerWithName("X-Auth-Token")
                                    .description("Authentication token retrieved from the authentication")),
                            pathParameters(
                                    parameterWithName("resumeId").description("Resume Identifier to compare against the Job"),
                                    parameterWithName("jobId").description("Job Identifier compare against the Resume")
                            ),
                            responseFields(
                                    fieldWithPath("resultCode").type(ResultCodes.class).description("Result code"),
                                    fieldWithPath("message").description("Message associated with the result code")
                            )
                    ))
                    .andReturn().getResponse();
            StarAnswer answer = new ObjectMapper().readValue(response.getContentAsString(), StarAnswer.class);
            assertEquals("Result code is not correct", ResultCodes.RESUME_NOT_FOUND, answer.getResultCode());
            assertEquals("Number of resumes should be 0", 0, answer.getStars().size());
        }
        @Test
        public void compareResult() throws Exception {

            Job job = new Job();
            job.setJobId("13");
            Resume resume = new Resume("12");

            Matcher matcher1 = Mockito.mock(Matcher.class);
            Mockito.when(matcher1.getCruncherName()).thenReturn("matcher1");
            Mockito.when(matcher1.matchSimilarity(resume, job)).thenReturn(3.2);
            Matcher matcher2 = Mockito.mock(Matcher.class);
            Mockito.when(matcher2.getCruncherName()).thenReturn("matcher2");
            Mockito.when(matcher2.matchSimilarity(resume, job)).thenReturn(4.1);

            List<Matcher> allMatchers = new ArrayList<>();
            allMatchers.add(matcher1);
            allMatchers.add(matcher2);
            Mockito.when(matcherList.getMatchers()).thenReturn(allMatchers);

            Mockito.when(jobRepository.findByJobId("13")).thenReturn(job);
            Mockito.when(resumeRepository.findByUserId("12")).thenReturn(resume);

            MockHttpServletResponse response = mockMvc.perform(get("/api/v2/resume/{resumeId}/compare/{jobId}", "12", "13")
                    .header("X-Auth-Token", token)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(document("resume/compare-success",
                            requestHeaders(headerWithName("X-Auth-Token")
                                    .description("Authentication token retrieved from the authentication")),
                            pathParameters(
                                    parameterWithName("resumeId").description("Resume Identifier to compare against the Job"),
                                    parameterWithName("jobId").description("Job Identifier compare against the Resume")
                            ),
                            responseFields(
                                    fieldWithPath("resultCode").type(ResultCodes.class).description("Result code"),
                                    fieldWithPath("message").description("Message associated with the result code"),
                                    fieldWithPath("stars").description("Hash with the star rating per cruncher")
                            )
                    ))
                    .andReturn().getResponse();
            StarAnswer answer = new ObjectMapper().readValue(response.getContentAsString(), StarAnswer.class);
            assertEquals("Result code is not correct", ResultCodes.SUCCESS, answer.getResultCode());
            assertEquals("Number of resumes should be 0", 2, answer.getStars().size());
            assertEquals("Stars for matcher1", 3.2, answer.getStars().get("matcher1"), 0);
            assertEquals("Stars for matcher2", 4.1, answer.getStars().get("matcher2"), 0);
        }
    }
}