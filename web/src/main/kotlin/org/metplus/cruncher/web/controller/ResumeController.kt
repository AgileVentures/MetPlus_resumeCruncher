package org.metplus.cruncher.web.controller

import org.metplus.cruncher.resume.Resume
import org.metplus.cruncher.resume.UploadResume
import org.metplus.cruncher.resume.UploadResumeObserver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping(value = [
    "/api/v1/resume",
    "/api/v2/resume"
])
class ResumeController(
        @Autowired private val uploadResume: UploadResume
) {

    @PostMapping("upload")
    @ResponseBody
    fun uploadResumeEndpoint(@RequestParam("userId") id: String,
                     @RequestParam("name") name: String,
                     @RequestParam("file") file: MultipartFile): CruncherResponse {
        System.out.println("Size is: " + file.size)
        return uploadResume.process(id, name,file.inputStream, file.size, observer =  object: UploadResumeObserver<CruncherResponse> {
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
}