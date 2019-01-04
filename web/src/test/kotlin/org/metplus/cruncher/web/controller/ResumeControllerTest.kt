package org.metplus.cruncher.web.controller

import org.hamcrest.Matchers
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.metplus.cruncher.job.Job
import org.metplus.cruncher.job.JobsRepository
import org.metplus.cruncher.rating.CruncherMetaData
import org.metplus.cruncher.rating.Matcher
import org.metplus.cruncher.rating.MatcherStub
import org.metplus.cruncher.rating.emptyMetaData
import org.metplus.cruncher.resume.Resume
import org.metplus.cruncher.resume.ResumeFile
import org.metplus.cruncher.resume.ResumeFileRepository
import org.metplus.cruncher.resume.ResumeRepository
import org.metplus.cruncher.resume.ResumeRepositoryFake
import org.metplus.cruncher.web.TestConfiguration
import org.metplus.cruncher.web.security.services.TokenService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.restdocs.headers.HeaderDocumentation.headerWithName
import org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.fileUpload
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.restdocs.request.RequestDocumentation.requestParameters
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [TestConfiguration::class, ResumeController::class], inheritLocations = true)
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
@AutoConfigureMockMvc
@WebMvcTest(ResumeController::class)
internal class ResumeControllerTest(@Autowired private val mvc: MockMvc) {

    @Autowired
    lateinit var resumeRepository: ResumeRepository
    @Autowired
    lateinit var resumeFileRepository: ResumeFileRepository
    @Autowired
    lateinit var jobRepository: JobsRepository
    @Autowired
    lateinit var matcher: Matcher<Resume, Job>

    @Autowired
    lateinit var tokenService: TokenService

    lateinit var token: String

    @BeforeEach
    fun setup() {
        token = tokenService.generateToken("0.0.0.0")
        (resumeRepository as ResumeRepositoryFake).removeAll()
    }

    @ParameterizedTest(name = "{index} => API Version: {0}")
    @ValueSource(strings = ["v1", "v2"])
    fun `when upload is successful, it returns success message`(versionId: String) {
        val file = javaClass.classLoader.getResourceAsStream("line_with_bold.pdf")
        val multipartFile = MockMultipartFile("file", file)

        mvc.perform(fileUpload("/api/$versionId/resume/upload")
                .file(multipartFile)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param("userId", "asdasdasd")
                .param("name", "line_with_bold.pdf")
                .header("X-Auth-Token", token)
        )
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.resultCode", Matchers.equalTo(ResultCodes.SUCCESS.toString())))
                .andDo(document("resume/upload",
                        requestHeaders(headerWithName("X-Auth-Token")
                                .description("Authentication token retrieved from the authentication")),
                        requestParameters(
                                parameterWithName("userId").description("Identifier of the resume"),
                                parameterWithName("name").description("Name of the resume file")
                        ),
                        responseFields(
                                fieldWithPath("resultCode").type(ResultCodes::class.java).description("Result code"),
                                fieldWithPath("message").description("Message associated with the result code")
                        )
                ))

    }

    @ParameterizedTest(name = "{index} => API Version: {0}")
    @ValueSource(strings = ["v1", "v2"])
    @Throws(Exception::class)
    fun `when download is successful, it returns the file`(versionId: String) {
        val file = javaClass.classLoader.getResourceAsStream("line_with_bold.pdf")

        resumeRepository.save(Resume(
                userId = "asdasdasd",
                filename = "line_with_bold.pdf",
                fileType = "pdf",
                cruncherData = CruncherMetaData(mutableMapOf())
        ))
        resumeFileRepository.save(ResumeFile(
                userId = "asdasdasd",
                filename = "line_with_bold.pdf",
                fileStream = file
        ))

        mvc.perform(
                get("/api/$versionId/resume/{userId}", "asdasdasd")
                        .header("X-Auth-Token", token))
                .andExpect(status().isOk)
                .andExpect(content().contentType("application/octet-stream"))
                .andDo(document("resume/download",
                        pathParameters(parameterWithName("userId").description("Resume identification")),
                        requestHeaders(headerWithName("X-Auth-Token")
                                .description("Authentication token retrieved from the authentication"))
                ))
                .andReturn()

    }

    @ParameterizedTest(name = "{index} => API Version: {0}")
    @ValueSource(strings = ["v1", "v2"])
    @Throws(Exception::class)
    fun `when trying to download a resume that does not exist, it returns an error`(versionId: String) {
        mvc.perform(get("/api/$versionId/resume/{userId}", "asdasdasd")
                .header("X-Auth-Token", token))
                .andExpect(status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$.resultCode", Matchers.equalTo(ResultCodes.FATAL_ERROR.toString())))
                .andDo(document("resume/download-error",
                        pathParameters(parameterWithName("userId").description("Resume identification")),
                        requestHeaders(headerWithName("X-Auth-Token")
                                .description("Authentication token retrieved from the authentication")),
                        responseFields(
                                fieldWithPath("resultCode").type(ResultCodes::class.java).description("Result code"),
                                fieldWithPath("message").description("Message associated with the result code")
                        )
                ))
                .andReturn().getResponse()
    }

    @ParameterizedTest(name = "{index} => API Version: {0}")
    @ValueSource(strings = ["v1", "v2"])
    @Throws(Exception::class)
    fun `when matching resumes with a job that does not exist, it returns an error`(versionId: String) {
        mvc.perform(get("/api/$versionId/resume/match/{jobId}", "asdasdasd")
                .header("X-Auth-Token", token))
                .andExpect(status().is4xxClientError)
                .andExpect(jsonPath("$.resultCode", Matchers.equalTo(ResultCodes.JOB_NOT_FOUND.toString())))
                .andDo(document("resume/match-job-not-found/v1",
                        requestHeaders(headerWithName("X-Auth-Token")
                                .description("Authentication token retrieved from the authentication")),
                        pathParameters(
                                parameterWithName("jobId").description("Job Identifier to retrieve the Resumes that match the job")
                        ),
                        responseFields(
                                fieldWithPath("resultCode").type(ResultCodes::class.java).description("Result code"),
                                fieldWithPath("message").description("Message associated with the result code")
                        )
                ))
    }

    @ParameterizedTest(name = "{index} => API Version: {0}")
    @ValueSource(strings = ["v1", "v2"])
    @Throws(Exception::class)
    fun `when matching resumes with a job that has multiple matches, it returns all the resumes that match`(versionId: String) {
        val resume1 = resumeRepository.save(Resume(
                userId = "resume-1",
                filename = "line_with_bold.pdf",
                fileType = "pdf",
                cruncherData = CruncherMetaData(mutableMapOf())
        ))
        resumeRepository.save(Resume(
                userId = "resume-2",
                filename = "line_with_bold.pdf",
                fileType = "pdf",
                cruncherData = CruncherMetaData(mutableMapOf())
        ))
        val resume2 = resumeRepository.save(Resume(
                userId = "resume-2",
                filename = "line_with_bold.pdf",
                fileType = "pdf",
                cruncherData = CruncherMetaData(mutableMapOf())
        ))
        jobRepository.save(Job("job-id", "title", "description", emptyMetaData(), emptyMetaData()))
        (matcher as MatcherStub).matchInverseReturnValue = listOf(resume2.copy(starRating = 1.0), resume1.copy(starRating = .1))

        mvc.perform(get("/api/$versionId/resume/match/{jobId}", "job-id")
                .header("X-Auth-Token", token))
                .andExpect(jsonPath("$.resultCode", Matchers.equalTo(ResultCodes.SUCCESS.toString())))
                .andExpect(jsonPath("$.resumes.naiveBayes[0].userId", Matchers.equalTo("resume-2")))
                .andExpect(jsonPath("$.resumes.naiveBayes[0].stars", Matchers.equalTo(1.0)))
                .andExpect(jsonPath("$.resumes.naiveBayes[1].userId", Matchers.equalTo("resume-1")))
                .andExpect(jsonPath("$.resumes.naiveBayes[1].stars", Matchers.equalTo(.1)))
                .andDo(document("resume/match-job-not-found/v1",
                        requestHeaders(headerWithName("X-Auth-Token")
                                .description("Authentication token retrieved from the authentication")),
                        pathParameters(
                                parameterWithName("jobId").description("Job Identifier to retrieve the Resumes that match the job")
                        ),
                        responseFields(
                                fieldWithPath("resultCode").type(ResultCodes::class.java).description("Result code"),
                                fieldWithPath("message").description("Message associated with the result code"),
                                subsectionWithPath("resumes").description("List with the identifiers of the resumes matched for each cruncher")
                        )
                ))
    }
}
