package org.metplus.cruncher.web.controller

import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.metplus.cruncher.job.Job
import org.metplus.cruncher.job.JobRepositoryFake
import org.metplus.cruncher.job.JobsRepository
import org.metplus.cruncher.web.TestConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.restdocs.request.RequestDocumentation.requestParameters
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [TestConfiguration::class, JobController::class], inheritLocations = true)
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
@AutoConfigureMockMvc
@WebMvcTest(JobController::class)
internal class JobControllerTest(@Autowired private val mvc: MockMvc) {
    @Autowired
    lateinit var jobsRepository: JobsRepository

    @BeforeEach
    fun setup() {
        (jobsRepository as JobRepositoryFake).removeAll()
    }

    @ParameterizedTest(name = "{index} => API Version: {0}")
    @ValueSource(strings = ["v1", "v2"])
    fun `When a job exists, it returns error and do not update the values`(versionId: String) {
        val job = Job("1", "some title", "some description")
        jobsRepository.save(job)

        createNewJob(
                versionId,
                job
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8"))
                .andExpect(jsonPath("$.resultCode", equalTo(ResultCodes.JOB_ID_EXISTS.toString())))
                .andDo(document("job/create-already-exists",
                        requestParameters(
                                parameterWithName("jobId").description("Job Identifier to create"),
                                parameterWithName("title").description("Title of the job"),
                                parameterWithName("description").description("Description of the job")
                        ),
                        responseFields(
                                fieldWithPath("resultCode").type(ResultCodes::class.java).description("Result code"),
                                fieldWithPath("message").description("Message associated with the result code")
                        )
                ))
        assertThat(jobsRepository.getById("1")).isEqualToComparingFieldByField(job)
    }

    @ParameterizedTest(name = "{index} => API Version: {0}")
    @ValueSource(strings = ["v1", "v2"])
    fun `when creating a job that does not exist, it returns success`(versionId: String) {
        createNewJob(versionId, Job("Job Identifier to create", "Title of the job", "Description of the job"))
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8"))
                .andExpect(jsonPath("$.resultCode", equalTo(ResultCodes.SUCCESS.toString())))
                .andDo(document("job/create-success",
                        requestParameters(
                                parameterWithName("jobId").description("Job Identifier to create"),
                                parameterWithName("title").description("Title of the job"),
                                parameterWithName("description").description("Description of the job")
                        ),
                        responseFields(
                                fieldWithPath("resultCode").type(ResultCodes::class.java).description("Result code"),
                                fieldWithPath("message").description("Message associated with the result code")
                        )
                ))
        val updatedJob = jobsRepository.getById("Job Identifier to create")
        assertThat(updatedJob).isNotNull
        assertThat(updatedJob!!.title).isEqualTo("Title of the job")
        assertThat(updatedJob.description).isEqualTo("Description of the job")
    }

    private fun createNewJob(versionId: String, job: Job): ResultActions {
        return mvc.perform(post("/api/$versionId/job/create")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param("jobId", job.id)
                .param("title", job.title)
                .param("description", job.description)
        )
    }

    @ParameterizedTest(name = "{index} => API Version: {0}")
    @ValueSource(strings = ["v1", "v2"])
    fun `when updating a job that does not exist, it returns an error`(versionId: String) {
        updateJob(versionId,
                "Job title", "My awsome job description")
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8"))
                .andExpect(jsonPath("$.resultCode", equalTo(ResultCodes.JOB_NOT_FOUND.toString())))
                .andDo(document("job/update-not-exists",
                        pathParameters(parameterWithName("jobId").description("Job Identifier to create")),
                        requestParameters(
                                parameterWithName("title").description("Title of the job"),
                                parameterWithName("description").description("Description of the job")
                        ),
                        responseFields(
                                fieldWithPath("resultCode").type(ResultCodes::class.java).description("Result code"),
                                fieldWithPath("message").description("Message associated with the result code")
                        )
                ))
    }

    @ParameterizedTest(name = "{index} => API Version: {0}")
    @ValueSource(strings = ["v1", "v2"])
    fun `when update a job that exists, it returns success`(versionId: String) {
        val job = Job("1", "My current title", "My current description")
        jobsRepository.save(job)

        updateJob(versionId,
                "Job title",
                "My awsome job description")
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8"))
                .andExpect(jsonPath("$.resultCode", equalTo(ResultCodes.SUCCESS.toString())))
                .andDo(document("job/update-success",
                        pathParameters(parameterWithName("jobId").description("Job Identifier to create")),
                        requestParameters(
                                parameterWithName("title").description("Title of the job(Optional)").optional(),
                                parameterWithName("description").description("Description of the job(Optional)").optional()
                        ),
                        responseFields(
                                fieldWithPath("resultCode").type(ResultCodes::class.java).description("Result code"),
                                fieldWithPath("message").description("Message associated with the result code")
                        )
                ))
    }

    private fun updateJob(versionId: String, jobTitle: String, jobDescription: String): ResultActions {
        return mvc.perform(patch("/api/$versionId/job/{jobId}/update", "1")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param("title", jobTitle)
                .param("description", jobDescription)
        )
    }
}