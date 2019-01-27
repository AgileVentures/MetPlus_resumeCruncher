package org.metplus.cruncher.web.controller.canned

import org.metplus.cruncher.canned.job.MatchWithResumeCanned
import org.metplus.cruncher.job.*
import org.metplus.cruncher.web.controller.CruncherResponse
import org.metplus.cruncher.web.controller.JobsMatchedAnswer
import org.metplus.cruncher.web.controller.ResultCodes
import org.metplus.cruncher.web.controller.toAllCrunchedJobsAnswer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(value = [
    "/api/v99999/job"
])
class JobCannedController(
        @Autowired private val createJob: CreateJob,
        @Autowired private val updateJob: UpdateJob,
        @Autowired private val matchWithResume: MatchWithResumeCanned,
        @Autowired private val reCrunchAllJobs: ReCrunchAllJobs
) {
    @PostMapping("create")
    @ResponseBody
    fun create(@RequestParam("jobId") id: String,
               @RequestParam("title") title: String,
               @RequestParam("description") description: String): CruncherResponse {
        var cruncherResponse: CruncherResponse? = null
        val jobToBeCreated = Job(id, title, description, mapOf(), mapOf())
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

    @GetMapping("match/{resumeId}")
    @ResponseBody
    fun match(@PathVariable("resumeId") resumeId: String): CruncherResponse {
        return matchWithResume.process(resumeId, object : MatchWithResumeObserver<CruncherResponse> {
            override fun success(matchedJobs: Map<String, List<Job>>): CruncherResponse {
                return JobsMatchedAnswer(
                        resultCode = ResultCodes.SUCCESS,
                        message = "Resume $resumeId matches ${matchedJobs.size} jobs",
                        jobs = matchedJobs.toAllCrunchedJobsAnswer()
                )
            }

            override fun resumeNotFound(resumeId: String): CruncherResponse {
                return CruncherResponse(
                        resultCode = ResultCodes.RESUME_NOT_FOUND,
                        message = "Resume $resumeId does not exist"
                )
            }

            override fun noMatches(resumeId: String, crunchers: List<String>): CruncherResponse {
                return JobsMatchedAnswer(
                        resultCode = ResultCodes.SUCCESS,
                        message = "Resume $resumeId as no matches",
                        jobs = crunchers.map { it to emptyList<Job>() }.toMap().toAllCrunchedJobsAnswer()
                )
            }

        })
    }

    @GetMapping("reindex")
    @ResponseBody
    fun reindex(): CruncherResponse {
        return reCrunchAllJobs.process(object : ReCrunchAllJobsObserver<CruncherResponse> {
            override fun onSuccess(numberScheduled: Int): CruncherResponse {
                return CruncherResponse(
                        resultCode = ResultCodes.SUCCESS,
                        message = "Going to reindex $numberScheduled jobs"
                )
            }

        })
    }
}