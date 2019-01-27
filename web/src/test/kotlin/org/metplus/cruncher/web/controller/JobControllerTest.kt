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
import org.metplus.cruncher.rating.Matcher
import org.metplus.cruncher.rating.MatcherStub
import org.metplus.cruncher.rating.emptyMetaData
import org.metplus.cruncher.resume.Resume
import org.metplus.cruncher.resume.ResumeRepository
import org.metplus.cruncher.resume.ResumeRepositoryFake
import org.metplus.cruncher.web.TestConfiguration
import org.metplus.cruncher.web.security.services.TokenService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.restdocs.headers.HeaderDocumentation.headerWithName
import org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath
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
    @Autowired
    lateinit var resumeRepository: ResumeRepository
    @Autowired
    lateinit var matcher: Matcher<Resume, Job>

    @Autowired
    lateinit var tokenService: TokenService

    lateinit var token: String

    @BeforeEach
    fun setup() {
        (jobsRepository as JobRepositoryFake).removeAll()
        (resumeRepository as ResumeRepositoryFake).removeAll()

        token = tokenService.generateToken("0.0.0.0")
    }

    @ParameterizedTest(name = "{index} => API Version: {0}")
    @ValueSource(strings = ["v1", "v2"])
    fun `When a job exists, it returns error and do not update the values`(versionId: String) {
        val job = Job("1", "some title", "some description", emptyMetaData(), emptyMetaData())
        jobsRepository.save(job)

        createNewJob(
                versionId,
                job
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8"))
                .andExpect(jsonPath("$.resultCode", equalTo(ResultCodes.JOB_ID_EXISTS.toString())))
                .andDo(document("job/create-already-exists/$versionId",
                        requestHeaders(headerWithName("X-Auth-Token")
                                .description("Authentication token retrieved from the authentication")),
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
        createNewJob(versionId, Job("Job Identifier to create", "Title of the job", "Description of the job", emptyMetaData(), emptyMetaData()))
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8"))
                .andExpect(jsonPath("$.resultCode", equalTo(ResultCodes.SUCCESS.toString())))
                .andDo(document("job/create-success/$versionId",
                        requestHeaders(headerWithName("X-Auth-Token")
                                .description("Authentication token retrieved from the authentication")),
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
                .header("X-Auth-Token", token)
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
                .andDo(document("job/update-not-exists/$versionId",
                        requestHeaders(headerWithName("X-Auth-Token")
                                .description("Authentication token retrieved from the authentication")),
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
        val job = Job("1", "My current title", "My current description", emptyMetaData(), emptyMetaData())
        jobsRepository.save(job)

        updateJob(versionId,
                "Job title",
                "My awsome job description")
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8"))
                .andExpect(jsonPath("$.resultCode", equalTo(ResultCodes.SUCCESS.toString())))
                .andDo(document("job/update-success/$versionId",
                        requestHeaders(headerWithName("X-Auth-Token")
                                .description("Authentication token retrieved from the authentication")),
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
                .header("X-Auth-Token", token)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param("title", jobTitle)
                .param("description", jobDescription)
        )
    }

    @ParameterizedTest(name = "{index} => API Version: {0}")
    @ValueSource(strings = ["v1", "v2"])
    fun `when matching jobs with a resume that does not exist, it returns resume not found error`(versionId: String) {
        matchJob(versionId, "1")
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8"))
                .andExpect(jsonPath("$.resultCode", equalTo(ResultCodes.RESUME_NOT_FOUND.toString())))
                .andDo(document("job/match-not-exists/$versionId",
                        requestHeaders(headerWithName("X-Auth-Token")
                                .description("Authentication token retrieved from the authentication")),
                        pathParameters(parameterWithName("resumeId").description("User identifier of the resume")),
                        responseFields(
                                fieldWithPath("resultCode").type(ResultCodes::class.java).description("Result code"),
                                fieldWithPath("message").description("Message associated with the result code")
                        )
                ))
    }

    @ParameterizedTest(name = "{index} => API Version: {0}")
    @ValueSource(strings = ["v1", "v2"])
    fun `when matching jobs with resume that does not match any, it returns success with an empty list of jobs`(versionId: String) {
        resumeRepository.save(Resume(
                "filename",
                "1",
                "pdf",
                mapOf("naiveBayes" to emptyMetaData())
        ))
        matchJob(versionId, "1")
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8"))
                .andExpect(jsonPath("$.resultCode", equalTo(ResultCodes.SUCCESS.toString())))
                .andExpect(jsonPath("$.jobs", equalTo(mapOf("naiveBayes" to emptyList<Job>()))))
                .andDo(document("job/match-not-found/$versionId",
                        requestHeaders(headerWithName("X-Auth-Token")
                                .description("Authentication token retrieved from the authentication")),
                        pathParameters(parameterWithName("resumeId").description("User identifier of the resume")),
                        responseFields(
                                fieldWithPath("resultCode").type(ResultCodes::class.java).description("Result code"),
                                fieldWithPath("message").description("Message associated with the result code"),
                                subsectionWithPath("jobs").type(Map::class.java).description("Hash with a list of job names matched by each cruncher")
                        )
                ))
    }

    @ParameterizedTest(name = "{index} => API Version: {0}")
    @ValueSource(strings = ["v1", "v2"])
    fun `when matching jobs with resume that does match one job, it returns success with a list with 1 job`(versionId: String) {
        resumeRepository.save(Resume(
                "filename",
                "1",
                "pdf",
                mapOf("naiveBayes" to emptyMetaData())
        ))
        val job = Job("1", "My current title", "My current description", emptyMetaData(), emptyMetaData())
        jobsRepository.save(job)
        jobsRepository.save(Job("2", "My other current title", "My other current description", emptyMetaData(), emptyMetaData()))
        jobsRepository.save(Job("3", "Another current title", "Another current description", emptyMetaData(), emptyMetaData()))
        (matcher as MatcherStub).matchReturnValue = listOf(job.copy(starRating = 3.1))

        matchJob(versionId, "1")
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8"))
                .andExpect(jsonPath("$.resultCode", equalTo(ResultCodes.SUCCESS.toString())))
                .andExpect(jsonPath("$.jobs.naiveBayes[0].id", equalTo("1")))
                .andExpect(jsonPath("$.jobs.naiveBayes[0].stars", equalTo(3.1)))
                .andDo(document("job/match-success/$versionId",
                        requestHeaders(headerWithName("X-Auth-Token")
                                .description("Authentication token retrieved from the authentication")),
                        pathParameters(parameterWithName("resumeId").description("User identifier of the resume")),
                        responseFields(
                                fieldWithPath("resultCode").type(ResultCodes::class.java).description("Result code"),
                                fieldWithPath("message").description("Message associated with the result code"),
                                subsectionWithPath("jobs").type(Map::class.java).description("Hash with a list of job names matched by each cruncher")
                        )
                ))
    }

    private fun matchJob(versionId: String, resumeId: String): ResultActions {
        return mvc.perform(get("/api/$versionId/job/match/{resumeId}", resumeId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .header("X-Auth-Token", token)
        )
    }


    @ParameterizedTest(name = "{index} => API Version: {0}")
    @ValueSource(strings = ["v1", "v2"])
    fun `when reindexing jobs, it returns information about of jobs to be processed`(versionId: String) {
        jobsRepository.save(Job("2", "My other current title", "My other current description", emptyMetaData(), emptyMetaData()))
        jobsRepository.save(Job("3", "Another current title", "Another current description", emptyMetaData(), emptyMetaData()))
        mvc.perform(get("/api/$versionId/job/reindex")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .header("X-Auth-Token", token))
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8"))
                .andExpect(jsonPath("$.resultCode", equalTo(ResultCodes.SUCCESS.toString())))
                .andExpect(jsonPath("$.message", equalTo("Going to reindex 2 jobs")))
                .andDo(document("job/match-not-found/$versionId",
                        requestHeaders(headerWithName("X-Auth-Token")
                                .description("Authentication token retrieved from the authentication")),
                        responseFields(
                                fieldWithPath("resultCode").type(ResultCodes::class.java).description("Result code"),
                                fieldWithPath("message").description("Message associated with the result code")
                        )
                ))
    }
}