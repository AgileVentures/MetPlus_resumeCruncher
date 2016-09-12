package org.metplus.curriculum.web.controllers;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.metplus.curriculum.api.WebMvcConfig;
import org.metplus.curriculum.cruncher.CruncherMetaData;
import org.metplus.curriculum.cruncher.Matcher;
import org.metplus.curriculum.cruncher.MatcherList;
import org.metplus.curriculum.database.domain.Job;
import org.metplus.curriculum.database.domain.MetaData;
import org.metplus.curriculum.database.domain.Resume;
import org.metplus.curriculum.database.repository.JobRepository;
import org.metplus.curriculum.database.repository.ResumeRepository;
import org.metplus.curriculum.process.JobCruncher;
import org.metplus.curriculum.test.MyStandaloneBuilder;
import org.metplus.curriculum.web.answers.ResultCodes;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.ConfigurableMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.setup.MockMvcConfigurer;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.Filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.junit.Assert.assertEquals;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests for the Job Controller
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({JobControllerTests.CreateEndpoint.class,
                     JobControllerTests.UpdateEndpoint.class,
                     JobControllerTests.MatchEndpointV1.class,
                     JobControllerTests.MatchEndpointV2.class})
public class JobControllerTests {
    public static class DefaultJobTest extends BaseControllerTest {

        @Autowired
        protected WebApplicationContext ctx;

        protected MockMvc mockMvc;

        @Mock
        protected JobRepository jobRepository;
        @Mock
        protected ResumeRepository resumeRepository;
        @Mock
        protected MatcherList matcherList;
        @Mock
        protected JobCruncher jobCruncher;

        @Autowired
        private Filter springSecurityFilterChain;

        @Rule
        public RestDocumentation restDocumentation = new RestDocumentation("build/generated-snippets");

        @Before
        public void setUp() throws Exception {
            MockitoAnnotations.initMocks(this);
            mockMvc = new MyStandaloneBuilder(new JobsController(jobRepository, resumeRepository, matcherList, jobCruncher), new WebMvcConfig())
                    .setValidator(validator())
                    .apply(documentationConfiguration(this.restDocumentation))
                    .build();

        }



        private LocalValidatorFactoryBean validator() {
            return new LocalValidatorFactoryBean();
        }
    }
    @RunWith(MockitoJUnitRunner.class)
    public static class CreateEndpoint extends DefaultJobTest {
        @Test
        public void alreadyExists() throws Exception {
            Job job = new Job();
            job.setJobId("1");
            Mockito.when(jobRepository.findByJobId("1")).thenReturn(job);

            mockMvc.perform(post("/api/v1/job/create")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                            .param("jobId", "1")
                            .param("title", "Job title")
                            .param("description", "My awsome job description")
                            .header("X-Auth-Token", "1234")
                    )
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8"))
                    .andExpect(jsonPath("$.resultCode", is(ResultCodes.JOB_ID_EXISTS.toString())))
                    .andDo(document("job/create-already-exists",
                            requestHeaders(headerWithName("X-Auth-Token")
                                    .description("Authentication token retrieved from the authentication")),
                            requestParameters(
                                    parameterWithName("jobId").description("Job Identifier to create"),
                                    parameterWithName("title").description("Title of the job"),
                                    parameterWithName("description").description("Description of the job")
                            ),
                            responseFields(
                                    fieldWithPath("resultCode").type(ResultCodes.class).description("Result code"),
                                    fieldWithPath("message").description("Message associated with the result code")
                            )
                    ));
            Mockito.verify(jobRepository).findByJobId("1");
            Mockito.verify(jobRepository, Mockito.times(0)).save(Mockito.any(Job.class));
            Mockito.verify(jobCruncher, Mockito.times(0)).addWork(Mockito.any(Job.class));
        }

        @Test
        public void success() throws Exception {
            Job job = new Job();
            job.setJobId("1");
            Mockito.when(jobRepository.findByJobId("1")).thenReturn(null);

            mockMvc.perform(post("/api/v1/job/create")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .param("jobId", "1")
                    .param("title", "Job title")
                    .param("description", "My awsome job description")
                    .header("X-Auth-Token", "1234")
            )
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8"))
                    .andExpect(jsonPath("$.resultCode", is(ResultCodes.SUCCESS.toString())))
                    .andDo(document("job/create-success",
                            requestHeaders(headerWithName("X-Auth-Token")
                                    .description("Authentication token retrieved from the authentication")),
                            requestParameters(
                                    parameterWithName("jobId").description("Job Identifier to create"),
                                    parameterWithName("title").description("Title of the job"),
                                    parameterWithName("description").description("Description of the job")
                            ),
                            responseFields(
                                    fieldWithPath("resultCode").type(ResultCodes.class).description("Result code"),
                                    fieldWithPath("message").description("Message associated with the result code")
                            )
                    ));
            Mockito.verify(jobRepository).findByJobId("1");
            ArgumentCaptor<Job> jobArgumentCaptor = ArgumentCaptor.forClass(Job.class);
            Mockito.verify(jobRepository).save(jobArgumentCaptor.capture());
            assertEquals("Job title", jobArgumentCaptor.getValue().getTitle());
            assertEquals("1", jobArgumentCaptor.getValue().getJobId());
            assertEquals("My awsome job description", jobArgumentCaptor.getValue().getDescription());
            ArgumentCaptor<Job> allJobs = ArgumentCaptor.forClass(Job.class);
            Mockito.verify(jobRepository).save(allJobs.capture());
            Mockito.verify(jobCruncher).addWork(allJobs.getValue());
        }
    }

    @RunWith(MockitoJUnitRunner.class)
    public static class UpdateEndpoint extends DefaultJobTest {
        @Test
        public void doNotExist() throws Exception {
            Mockito.when(jobRepository.findByJobId("1")).thenReturn(null);

            mockMvc.perform(patch("/api/v1/job/1/update")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .param("jobId", "1")
                    .param("title", "Job title")
                    .param("description", "My awsome job description")
                    .header("X-Auth-Token", "1234")
            )
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8"))
                    .andExpect(jsonPath("$.resultCode", is(ResultCodes.JOB_NOT_FOUND.toString())))
                    .andDo(document("job/update-not-exists",
                            requestHeaders(headerWithName("X-Auth-Token")
                                    .description("Authentication token retrieved from the authentication")),
                            requestParameters(
                                    parameterWithName("jobId").description("Job Identifier to create"),
                                    parameterWithName("title").description("Title of the job"),
                                    parameterWithName("description").description("Description of the job")
                            ),
                            responseFields(
                                    fieldWithPath("resultCode").type(ResultCodes.class).description("Result code"),
                                    fieldWithPath("message").description("Message associated with the result code")
                            )
                    ));
            Mockito.verify(jobRepository).findByJobId("1");
            Mockito.verify(jobRepository, Mockito.times(0)).save((Job) Mockito.any());
            Mockito.verify(jobRepository, Mockito.times(0)).save(Mockito.any(Job.class));
            Mockito.verify(jobCruncher, Mockito.times(0)).addWork(Mockito.any(Job.class));
        }

        @Test
        public void success() throws Exception {
            Job job = new Job();
            job.setJobId("1");
            job.setTitle("My current title");
            job.setDescription("My current description");
            Mockito.when(jobRepository.findByJobId("1")).thenReturn(job);

            mockMvc.perform(patch("/api/v1/job/1/update")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .param("jobId", "1")
                    .param("title", "Job title")
                    .param("description", "My awsome job description")
                    .header("X-Auth-Token", "1234")
            )
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8"))
                    .andExpect(jsonPath("$.resultCode", is(ResultCodes.SUCCESS.toString())))
                    .andDo(document("job/update-success",
                            requestHeaders(headerWithName("X-Auth-Token")
                                    .description("Authentication token retrieved from the authentication")),
                            requestParameters(
                                    parameterWithName("jobId").description("Job Identifier to create"),
                                    parameterWithName("title").description("Title of the job(Optional)"),
                                    parameterWithName("description").description("Description of the job(Optional)")
                            ),
                            responseFields(
                                    fieldWithPath("resultCode").type(ResultCodes.class).description("Result code"),
                                    fieldWithPath("message").description("Message associated with the result code")
                            )
                    ));
            Mockito.verify(jobRepository).findByJobId("1");
            ArgumentCaptor<Job> jobArgumentCaptor = ArgumentCaptor.forClass(Job.class);
            Mockito.verify(jobRepository).save(jobArgumentCaptor.capture());
            assertEquals("Job title", jobArgumentCaptor.getValue().getTitle());
            assertEquals("1", jobArgumentCaptor.getValue().getJobId());
            assertEquals("My awsome job description", jobArgumentCaptor.getValue().getDescription());
            ArgumentCaptor<Job> allJobs = ArgumentCaptor.forClass(Job.class);
            Mockito.verify(jobRepository).save(allJobs.capture());
            Mockito.verify(jobCruncher).addWork(allJobs.getValue());
        }
        @Test
        public void successOnlyTitle() throws Exception {
            Job job = new Job();
            job.setJobId("1");
            job.setTitle("My current title");
            job.setDescription("My current description");
            Mockito.when(jobRepository.findByJobId("1")).thenReturn(job);

            mockMvc.perform(patch("/api/v1/job/1/update")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .param("jobId", "1")
                    .param("title", "Job title")
                    .header("X-Auth-Token", "1234")
            )
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8"))
                    .andExpect(jsonPath("$.resultCode", is(ResultCodes.SUCCESS.toString())));
            Mockito.verify(jobRepository).findByJobId("1");
            ArgumentCaptor<Job> jobArgumentCaptor = ArgumentCaptor.forClass(Job.class);
            Mockito.verify(jobRepository).save(jobArgumentCaptor.capture());
            assertEquals("Job title", jobArgumentCaptor.getValue().getTitle());
            assertEquals("1", jobArgumentCaptor.getValue().getJobId());
            assertEquals("My current description", jobArgumentCaptor.getValue().getDescription());
            ArgumentCaptor<Job> allJobs = ArgumentCaptor.forClass(Job.class);
            Mockito.verify(jobRepository).save(allJobs.capture());
            Mockito.verify(jobCruncher).addWork(allJobs.getValue());
        }

        @Test
        public void successOnlyDescription() throws Exception {
            Job job = new Job();
            job.setJobId("1");
            job.setTitle("My current title");
            job.setDescription("My current description");
            Mockito.when(jobRepository.findByJobId("1")).thenReturn(job);

            mockMvc.perform(patch("/api/v1/job/1/update")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .param("jobId", "1")
                    .param("description", "My awsome job description")
                    .header("X-Auth-Token", "1234")
            )
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8"))
                    .andExpect(jsonPath("$.resultCode", is(ResultCodes.SUCCESS.toString())));
            Mockito.verify(jobRepository).findByJobId("1");
            ArgumentCaptor<Job> jobArgumentCaptor = ArgumentCaptor.forClass(Job.class);
            Mockito.verify(jobRepository).save(jobArgumentCaptor.capture());
            assertEquals("My current title", jobArgumentCaptor.getValue().getTitle());
            assertEquals("1", jobArgumentCaptor.getValue().getJobId());
            assertEquals("My awsome job description", jobArgumentCaptor.getValue().getDescription());
            ArgumentCaptor<Job> allJobs = ArgumentCaptor.forClass(Job.class);
            Mockito.verify(jobRepository).save(allJobs.capture());
            Mockito.verify(jobCruncher).addWork(allJobs.getValue());
        }
    }

    @RunWith(MockitoJUnitRunner.class)
    public static class MatchEndpointV1 extends DefaultJobTest {
        @Test
        public void doNotExist() throws Exception {
            Mockito.when(resumeRepository.findByUserId("1")).thenReturn(null);

            mockMvc.perform(
                    RestDocumentationRequestBuilders.get("/api/v1/job/match/{resumeId}", "1")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .header("X-Auth-Token", "1234")
            )
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8"))
                    .andExpect(jsonPath("$.resultCode", is(ResultCodes.RESUME_NOT_FOUND.toString())))
                    .andDo(document("job/match-not-exists/v1",
                            requestHeaders(headerWithName("X-Auth-Token")
                                    .description("Authentication token retrieved from the authentication")),
                            pathParameters(parameterWithName("resumeId").description("User identifier of the resume")),
                            responseFields(
                                    fieldWithPath("resultCode").type(ResultCodes.class).description("Result code"),
                                    fieldWithPath("message").description("Message associated with the result code")
                            )
                    ));
            Mockito.verify(resumeRepository).findByUserId("1");
        }
        @Test
        public void success() throws Exception {
            Map<String, MetaData> metaData1 = new HashMap<>();
            metaData1.put("matcher1", new MetaData());
            metaData1.put("matcher2", new MetaData());
            Resume resume = new Resume("1");
            resume.setMetaData(metaData1);
            Mockito.when(resumeRepository.findByUserId("1")).thenReturn(resume);

            Job job1 = new Job();
            job1.setJobId("1");
            job1.setStarRating(4.2);
            Job job2 = new Job();
            job2.setStarRating(1.0);
            job2.setJobId("2");
            Job job3 = new Job();
            job3.setJobId("3");
            job3.setStarRating(3.);
            Job job4 = new Job();
            job4.setJobId("2");
            job4.setStarRating(0.5);
            List<Job> matcher1Resumes = new ArrayList<>();
            matcher1Resumes.add(job1);
            matcher1Resumes.add(job2);
            List<Job> matcher2Resumes = new ArrayList<>();
            matcher2Resumes.add(job3);
            matcher2Resumes.add(job4);

            Matcher matcher1 = Mockito.mock(Matcher.class);
            Mockito.when(matcher1.getCruncherName()).thenReturn("matcher1");
            Mockito.when(matcher1.match(Mockito.any(CruncherMetaData.class))).thenReturn(matcher1Resumes);
            Matcher matcher2 = Mockito.mock(Matcher.class);
            Mockito.when(matcher2.getCruncherName()).thenReturn("matcher2");
            Mockito.when(matcher2.match(Mockito.any(CruncherMetaData.class))).thenReturn(matcher2Resumes);

            List<Matcher> allMatchers = new ArrayList<>();
            allMatchers.add(matcher1);
            allMatchers.add(matcher2);
            Mockito.when(matcherList.getMatchers()).thenReturn(allMatchers);


            mockMvc.perform(
                    RestDocumentationRequestBuilders.get("/api/v1/job/match/{resumeId}", "1")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                            .header("X-Auth-Token", "1234")
            )
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8"))
                    .andExpect(jsonPath("$.resultCode", is(ResultCodes.SUCCESS.toString())))
                    .andExpect(jsonPath("$.jobs.matcher1", hasSize(2)))
                    .andExpect(jsonPath("$.jobs.matcher1[0]", is("1")))
                    .andExpect(jsonPath("$.jobs.matcher1[1]", is("2")))
                    .andExpect(jsonPath("$.jobs.matcher2", hasSize(2)))
                    .andExpect(jsonPath("$.jobs.matcher2[0]", is("3")))
                    .andExpect(jsonPath("$.jobs.matcher2[1]", is("2")))
                    .andDo(document("job/match-success/v1",
                            requestHeaders(headerWithName("X-Auth-Token")
                                    .description("Authentication token retrieved from the authentication")),
                            pathParameters(parameterWithName("resumeId").description("User identifier of the resume")),
                            responseFields(
                                    fieldWithPath("resultCode").type(ResultCodes.class).description("Result code"),
                                    fieldWithPath("message").description("Message associated with the result code"),
                                    fieldWithPath("jobs").description("Hash with a list of job IDs matched by each cruncher")
                            )
                    ));
            Mockito.verify(resumeRepository).findByUserId("1");
            Mockito.verify(matcher1).match(Mockito.any(CruncherMetaData.class));
            Mockito.verify(matcher2).match(Mockito.any(CruncherMetaData.class));
        }
        @Test
        public void noMatches() throws Exception {
            Map<String, MetaData> metaData1 = new HashMap<>();
            metaData1.put("matcher1", new MetaData());
            metaData1.put("matcher2", new MetaData());
            Resume resume = new Resume("1");
            resume.setMetaData(metaData1);
            Mockito.when(resumeRepository.findByUserId("1")).thenReturn(resume);

            Matcher matcher1 = Mockito.mock(Matcher.class);
            Mockito.when(matcher1.getCruncherName()).thenReturn("matcher1");
            Mockito.when(matcher1.match(Mockito.any(CruncherMetaData.class))).thenReturn(new ArrayList<>());
            Matcher matcher2 = Mockito.mock(Matcher.class);
            Mockito.when(matcher2.getCruncherName()).thenReturn("matcher2");
            Mockito.when(matcher2.match(Mockito.any(CruncherMetaData.class))).thenReturn(new ArrayList<>());

            List<Matcher> allMatchers = new ArrayList<>();
            allMatchers.add(matcher1);
            allMatchers.add(matcher2);
            Mockito.when(matcherList.getMatchers()).thenReturn(allMatchers);


            mockMvc.perform(
                    RestDocumentationRequestBuilders.get("/api/v1/job/match/{resumeId}", "1")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                            .header("X-Auth-Token", "1234")
            )
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8"))
                    .andExpect(jsonPath("$.resultCode", is(ResultCodes.SUCCESS.toString())))
                    .andExpect(jsonPath("$.jobs").value(is(new java.util.LinkedHashMap())))
                    .andDo(document("job/match-not-found/v1",
                            requestHeaders(headerWithName("X-Auth-Token")
                                    .description("Authentication token retrieved from the authentication")),
                            pathParameters(parameterWithName("resumeId").description("User identifier of the resume")),
                            responseFields(
                                    fieldWithPath("resultCode").type(ResultCodes.class).description("Result code"),
                                    fieldWithPath("message").description("Message associated with the result code"),
                                    fieldWithPath("jobs").description("Hash with a list of job ids matched by each cruncher")
                            )
                    ));
            Mockito.verify(resumeRepository).findByUserId("1");
            Mockito.verify(matcher1).match(Mockito.any(CruncherMetaData.class));
            Mockito.verify(matcher2).match(Mockito.any(CruncherMetaData.class));
        }
    }


    @RunWith(MockitoJUnitRunner.class)
    public static class MatchEndpointV2 extends DefaultJobTest {
        @Test
        public void doNotExist() throws Exception {
            Mockito.when(resumeRepository.findByUserId("1")).thenReturn(null);

            mockMvc.perform(
                    RestDocumentationRequestBuilders.get("/api/v2/job/match/{resumeId}", "1")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                            .header("X-Auth-Token", "1234")
            )
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8"))
                    .andExpect(jsonPath("$.resultCode", is(ResultCodes.RESUME_NOT_FOUND.toString())))
                    .andDo(document("job/match-not-exists/v2",
                            requestHeaders(headerWithName("X-Auth-Token")
                                    .description("Authentication token retrieved from the authentication")),
                            pathParameters(parameterWithName("resumeId").description("User identifier of the resume")),
                            responseFields(
                                    fieldWithPath("resultCode").type(ResultCodes.class).description("Result code"),
                                    fieldWithPath("message").description("Message associated with the result code")
                            )
                    ));
            Mockito.verify(resumeRepository).findByUserId("1");
        }
        @Test
        public void success() throws Exception {
            Map<String, MetaData> metaData1 = new HashMap<>();
            metaData1.put("matcher1", new MetaData());
            metaData1.put("matcher2", new MetaData());
            Resume resume = new Resume("1");
            resume.setMetaData(metaData1);
            Mockito.when(resumeRepository.findByUserId("1")).thenReturn(resume);

            Job job1 = new Job();
            job1.setJobId("1");
            job1.setStarRating(4.2);
            Job job2 = new Job();
            job2.setStarRating(1.0);
            job2.setJobId("2");
            Job job3 = new Job();
            job3.setJobId("3");
            job3.setStarRating(3.);
            Job job4 = new Job();
            job4.setJobId("2");
            job4.setStarRating(0.5);
            List<Job> matcher1Resumes = new ArrayList<>();
            matcher1Resumes.add(job1);
            matcher1Resumes.add(job2);
            List<Job> matcher2Resumes = new ArrayList<>();
            matcher2Resumes.add(job3);
            matcher2Resumes.add(job4);

            Matcher matcher1 = Mockito.mock(Matcher.class);
            Mockito.when(matcher1.getCruncherName()).thenReturn("matcher1");
            Mockito.when(matcher1.match(Mockito.any(CruncherMetaData.class))).thenReturn(matcher1Resumes);
            Matcher matcher2 = Mockito.mock(Matcher.class);
            Mockito.when(matcher2.getCruncherName()).thenReturn("matcher2");
            Mockito.when(matcher2.match(Mockito.any(CruncherMetaData.class))).thenReturn(matcher2Resumes);

            List<Matcher> allMatchers = new ArrayList<>();
            allMatchers.add(matcher1);
            allMatchers.add(matcher2);
            Mockito.when(matcherList.getMatchers()).thenReturn(allMatchers);


            mockMvc.perform(
                    RestDocumentationRequestBuilders.get("/api/v2/job/match/{resumeId}", "1")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                            .header("X-Auth-Token", "1234")
            )
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8"))
                    .andExpect(jsonPath("$.resultCode", is(ResultCodes.SUCCESS.toString())))
                    .andExpect(jsonPath("$.jobs.matcher1", hasSize(2)))
                    .andExpect(jsonPath("$.jobs.matcher1[0].jobId", is("1")))
                    .andExpect(jsonPath("$.jobs.matcher1[0].stars", is(4.2)))
                    .andExpect(jsonPath("$.jobs.matcher1[1].jobId", is("2")))
                    .andExpect(jsonPath("$.jobs.matcher1[1].stars", is(1.0)))
                    .andExpect(jsonPath("$.jobs.matcher2", hasSize(2)))
                    .andExpect(jsonPath("$.jobs.matcher2[0].jobId", is("3")))
                    .andExpect(jsonPath("$.jobs.matcher2[0].stars", is(3.)))
                    .andExpect(jsonPath("$.jobs.matcher2[1].jobId", is("2")))
                    .andExpect(jsonPath("$.jobs.matcher2[1].stars", is(.5)))
                    .andDo(document("job/match-success/v2",
                            requestHeaders(headerWithName("X-Auth-Token")
                                    .description("Authentication token retrieved from the authentication")),
                            pathParameters(parameterWithName("resumeId").description("User identifier of the resume")),
                            responseFields(
                                    fieldWithPath("resultCode").type(ResultCodes.class).description("Result code"),
                                    fieldWithPath("message").description("Message associated with the result code"),
                                    fieldWithPath("jobs").description("Hash with a list of job IDs and the star rating matched by each cruncher")
                            )
                    ));
            Mockito.verify(resumeRepository).findByUserId("1");
            Mockito.verify(matcher1).match(Mockito.any(CruncherMetaData.class));
            Mockito.verify(matcher2).match(Mockito.any(CruncherMetaData.class));
        }
        @Test
        public void noMatches() throws Exception {
            Map<String, MetaData> metaData1 = new HashMap<>();
            metaData1.put("matcher1", new MetaData());
            metaData1.put("matcher2", new MetaData());
            Resume resume = new Resume("1");
            resume.setMetaData(metaData1);
            Mockito.when(resumeRepository.findByUserId("1")).thenReturn(resume);

            Matcher matcher1 = Mockito.mock(Matcher.class);
            Mockito.when(matcher1.getCruncherName()).thenReturn("matcher1");
            Mockito.when(matcher1.match(Mockito.any(CruncherMetaData.class))).thenReturn(new ArrayList<>());
            Matcher matcher2 = Mockito.mock(Matcher.class);
            Mockito.when(matcher2.getCruncherName()).thenReturn("matcher2");
            Mockito.when(matcher2.match(Mockito.any(CruncherMetaData.class))).thenReturn(new ArrayList<>());

            List<Matcher> allMatchers = new ArrayList<>();
            allMatchers.add(matcher1);
            allMatchers.add(matcher2);
            Mockito.when(matcherList.getMatchers()).thenReturn(allMatchers);


            mockMvc.perform(
                    RestDocumentationRequestBuilders.get("/api/v2/job/match/{resumeId}", "1")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                            .header("X-Auth-Token", "1234")
            )
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8"))
                    .andExpect(jsonPath("$.resultCode", is(ResultCodes.SUCCESS.toString())))
                    .andExpect(jsonPath("$.jobs").value(is(new java.util.LinkedHashMap())))
                    .andDo(document("job/match-not-found/v2",
                            requestHeaders(headerWithName("X-Auth-Token")
                                    .description("Authentication token retrieved from the authentication")),
                            pathParameters(parameterWithName("resumeId").description("User identifier of the resume")),
                            responseFields(
                                    fieldWithPath("resultCode").type(ResultCodes.class).description("Result code"),
                                    fieldWithPath("message").description("Message associated with the result code"),
                                    fieldWithPath("jobs").description("Hash with a list of job names matched by each cruncher")
                            )
                    ));
            Mockito.verify(resumeRepository).findByUserId("1");
            Mockito.verify(matcher1).match(Mockito.any(CruncherMetaData.class));
            Mockito.verify(matcher2).match(Mockito.any(CruncherMetaData.class));
        }
    }
}
