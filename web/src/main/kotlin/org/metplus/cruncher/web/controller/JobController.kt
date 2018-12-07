package org.metplus.cruncher.web.controller

import org.metplus.cruncher.job.CreateJob
import org.metplus.cruncher.job.CreateJobObserver
import org.metplus.cruncher.job.Job
import org.metplus.cruncher.job.UpdateJob
import org.metplus.cruncher.job.UpdateJobObserver
import org.metplus.cruncher.rating.CruncherMetaData
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = [
    "/api/v1/job",
    "/api/v2/job"
])
class JobController(
        @Autowired private val createJob: CreateJob,
        @Autowired private val updateJob: UpdateJob
) {

    @PostMapping("create")
    @ResponseBody
    fun create(@RequestParam("jobId") id: String,
               @RequestParam("title") title: String,
               @RequestParam("description") description: String): CruncherResponse {
        var cruncherResponse: CruncherResponse? = null
        val jobToBeCreated = Job(id, title, description, CruncherMetaData(mutableMapOf()), CruncherMetaData(mutableMapOf()))
        createJob.process(jobToBeCreated, observer = object : CreateJobObserver {
            override fun onSuccess(job: Job) {
                cruncherResponse = CruncherResponse(ResultCodes.SUCCESS, "Job added successfully")
            }

            override fun onAlreadyExists() {
                cruncherResponse = CruncherResponse(ResultCodes.JOB_ID_EXISTS, "Trying to create job that already exists")
            }

        })

        return cruncherResponse!!
    }

    @PatchMapping("{jobId}/update")
    @ResponseBody
    fun update(@PathVariable("jobId") jobId: String,
               @RequestParam(value = "title", required = false) title: String?,
               @RequestParam(value = "description", required = false) description: String?): CruncherResponse {
        var cruncherResponse: CruncherResponse? = null
        updateJob.process(jobId, title, description, object : UpdateJobObserver {
            override fun onSuccess(job: Job) {
                cruncherResponse = CruncherResponse(
                        resultCode = ResultCodes.SUCCESS,
                        message = "Job updated successfully"
                )
            }

            override fun onNotFound() {
                cruncherResponse = CruncherResponse(
                        resultCode = ResultCodes.JOB_NOT_FOUND,
                        message = "Job not found"
                )
            }

        })
        return cruncherResponse!!
    }
}
