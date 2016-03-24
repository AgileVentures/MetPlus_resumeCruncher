package org.metplus.curriculum.web.controllers;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.metplus.curriculum.database.domain.Job;
import org.metplus.curriculum.database.repository.JobRepository;
import org.metplus.curriculum.web.ResultCodes;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentation;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.Filter;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Created by joao on 3/24/16.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({JobControllerTests.CreateEndpoint.class,
                     JobControllerTests.UpdateEndpoint.class})
public class JobControllerTests {

    @Configuration
    public static class JobControllerConfig {
        @Bean
        public JobRepository jobRepository() {
            return Mockito.mock(JobRepository.class);
        }
    }
    public static class DefaultJobTest extends BaseControllerTest {
        @Autowired
        protected WebApplicationContext ctx;

        protected MockMvc mockMvc;
        @Value("${backend.admin.username}")
        protected String backendAdminUsername;

        @Value("${backend.admin.password}")
        protected String backendAdminPassword;

        @Mock
        protected JobRepository jobRepository;

        @Autowired
        private Filter springSecurityFilterChain;

        protected String token;

        @Rule
        public RestDocumentation restDocumentation = new RestDocumentation("build/generated-snippets");

        @Before
        public void setUp() throws Exception {

            mockMvc = MockMvcBuilders.standaloneSetup(new JobsController(jobRepository))
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
            Mockito.verify(jobRepository, Mockito.times(0)).save((Job) Mockito.any());
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
        }
    }
}
