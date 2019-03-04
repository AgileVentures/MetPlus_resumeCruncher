package org.metplus.cruncher.web.controller

import org.metplus.cruncher.rating.CompareResumeWithJob
import org.metplus.cruncher.rating.CompareResumeWithJobObserver
import org.metplus.cruncher.resume.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.util.FileCopyUtils
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.ByteArrayOutputStream
import java.text.DecimalFormat
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping(value = [
    "/api/v1/resume",
    "/api/v2/resume"
])
class ResumeController(
        @Autowired private val uploadResume: UploadResume,
        @Autowired private val downloadResume: DownloadResume,
        @Autowired private val matchWithJob: MatchWithJob,
        @Autowired private val reCrunchAllResumes: ReCrunchAllResumes,
        @Autowired private val compareResumeWithJob: CompareResumeWithJob
) {

    @PostMapping("upload")
    @ResponseBody
    fun uploadResumeEndpoint(@RequestParam("userId") id: String,
                             @RequestParam("name") name: String,
                             @RequestParam("file") file: MultipartFile): CruncherResponse {
        return uploadResume.process(id, name, file.inputStream, file.size, observer = object : UploadResumeObserver<CruncherResponse> {
            override fun onException(exception: Exception, resume: Resume): CruncherResponse {
                return CruncherResponse(
                        resultCode = ResultCodes.FATAL_ERROR,
                        message = "Exception happened while uploading the resume"
                )
            }

            override fun onSuccess(resume: Resume): CruncherResponse {
                return CruncherResponse(
                        resultCode = ResultCodes.SUCCESS,
                        message = "Resume upload successful"
                )
            }

            override fun onEmptyFile(resume: Resume): CruncherResponse {
                return CruncherResponse(
                        resultCode = ResultCodes.FATAL_ERROR,
                        message = "Resume is empty"
                )
            }

        }) as CruncherResponse
    }

    @GetMapping("{userId}")
    @ResponseBody
    fun downloadResumeEndpoint(@PathVariable("userId") id: String, response: HttpServletResponse): CruncherResponse? {
        return downloadResume.process(id, observer = object : DownloadResumeObserver<CruncherResponse?> {
            override fun onError(userId: String, exception: Exception): CruncherResponse {
                return CruncherResponse(
                        resultCode = ResultCodes.FATAL_ERROR,
                        message = "Exception happened while uploading the resume"
                )
            }

            override fun onSuccess(resume: Resume, resumeFile: ResumeFile): CruncherResponse? {
                val outputFile = ByteArrayOutputStream()
                var data = resumeFile.fileStream.read()
                while (data >= 0) {
                    outputFile.write(data.toChar().toInt())
                    data = resumeFile.fileStream.read()
                }
                outputFile.flush()
                response.contentType = "application/octet-stream"
                response.setHeader("Content-Disposition", "inline; filename=\"" + resumeFile.filename + "\"")
                FileCopyUtils.copy(outputFile.toByteArray(), response.outputStream)
                response.setContentLength(outputFile.size())
                response.flushBuffer()
                return null
            }

            override fun onResumeNotFound(userId: String): CruncherResponse {
                return CruncherResponse(
                        resultCode = ResultCodes.FATAL_ERROR,
                        message = "Resume is empty"
                )
            }

        })
    }

    @GetMapping("/match/{jobId}")
    @ResponseBody
    fun downloadResumeEndpoint(@PathVariable("jobId") id: String): ResponseEntity<CruncherResponse> {
        return matchWithJob.process(jobId = id, observer = object : MatchWithJobObserver<ResponseEntity<CruncherResponse>> {
            override fun jobNotFound(jobId: String): ResponseEntity<CruncherResponse> {
                return ResponseEntity(CruncherResponse(
                        resultCode = ResultCodes.JOB_NOT_FOUND,
                        message = "Job with id '$jobId' was not found"
                ), HttpStatus.NOT_FOUND)
            }

            override fun noMatchFound(jobId: String, matchers: Map<String, List<Resume>>): ResponseEntity<CruncherResponse> {
                return ResponseEntity(ResumeMatchedAnswer(
                        resultCode = ResultCodes.SUCCESS,
                        message = "Job with id '$jobId' has no matches",
                        resumes = matchers.toAllCrunchedResumesAnswer()
                ), HttpStatus.OK)
            }

            override fun success(matchedResumes: Map<String, List<Resume>>): ResponseEntity<CruncherResponse> {
                return ResponseEntity(ResumeMatchedAnswer(
                        resultCode = ResultCodes.SUCCESS,
                        message = "Job matches ${matchedResumes.size} resumes",
                        resumes = matchedResumes.toAllCrunchedResumesAnswer()
                ), HttpStatus.OK)
            }
        })
    }

    @GetMapping("{resumeId}/compare/{jobId}")
    @ResponseBody
    fun compare(@PathVariable("resumeId") resumeId: String, @PathVariable("jobId") jobId: String): ResponseEntity<CruncherResponse> {
        return compareResumeWithJob.process(resumeId, jobId, object : CompareResumeWithJobObserver<ResponseEntity<CruncherResponse>> {
            override fun onJobNotFound(jobId: String): ResponseEntity<CruncherResponse> {
                return ResponseEntity.ok(CruncherResponse(
                        resultCode = ResultCodes.JOB_NOT_FOUND,
                        message = "Job $jobId was not found"))
            }

            override fun onResumeNotFound(resumeId: String): ResponseEntity<CruncherResponse> {
                return ResponseEntity(CruncherResponse(
                        resultCode = ResultCodes.RESUME_NOT_FOUND,
                        message = "Resume $resumeId was not found"), HttpStatus.NOT_FOUND)
            }

            override fun onSuccess(starsRating: Double): ResponseEntity<CruncherResponse> {
                return ResponseEntity.ok(ComparisonMatchAnswer(
                        message = "Job and Resume match",
                        stars = mapOf("naiveBayes" to starsRating)
                ))
            }
        })
    }

    @GetMapping("/reindex")
    @ResponseBody
    fun reindex(): CruncherResponse {
        return reCrunchAllResumes.process(object : ReCrunchAllResumesObserver<CruncherResponse> {
            override fun onSuccess(numberScheduled: Int): CruncherResponse {
                return CruncherResponse(
                        resultCode = ResultCodes.SUCCESS,
                        message = "Going to reindex $numberScheduled resumes"
                )
            }
        })
    }
}

fun Map<String, List<Resume>>.toAllCrunchedResumesAnswer(): Map<String, List<ResumeAnswer>> {
    val response = mutableMapOf<String, List<ResumeAnswer>>()
    keys.forEach {
        response[it] = get(it)!!.map { it.toResumeAnswer() }
    }
    return response
}

fun Resume.toResumeAnswer(): ResumeAnswer {
    return ResumeAnswer(
            filename,
            userId,
            fileType,
            DecimalFormat("#.##").format(starRating).toDouble()
    )
}

class ResumeMatchedAnswer(
        resultCode: ResultCodes,
        message: String,
        val resumes: Map<String, List<ResumeAnswer>>
) : CruncherResponse(
        resultCode,
        message)

data class ResumeAnswer(
        val filename: String,
        val resumeId: String,
        val fileType: String,
        val stars: Double
)

class ComparisonMatchAnswer(
        message: String,
        val stars: Map<String, Double>
) : CruncherResponse(
        ResultCodes.SUCCESS,
        message)
