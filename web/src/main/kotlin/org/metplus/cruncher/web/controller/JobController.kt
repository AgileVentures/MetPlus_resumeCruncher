package org.metplus.cruncher.web.controller

import org.metplus.cruncher.job.CreateJob
import org.metplus.cruncher.job.CreateJobObserver
import org.metplus.cruncher.job.Job
import org.metplus.cruncher.job.MatchWithResume
import org.metplus.cruncher.job.MatchWithResumeObserver
import org.metplus.cruncher.job.ReCrunchAllJobs
import org.metplus.cruncher.job.ReCrunchAllJobsObserver
import org.metplus.cruncher.job.UpdateJob
import org.metplus.cruncher.job.UpdateJobObserver
import org.metplus.cruncher.rating.CruncherMetaData
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
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
        @Autowired private val updateJob: UpdateJob,
        @Autowired private val matchWithResume: MatchWithResume,
        @Autowired private val reCrunchAllJobs: ReCrunchAllJobs
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

    @GetMapping("match/{resumeId}")
    @ResponseBody
    fun match(@PathVariable("resumeId") resumeId: String): CruncherResponse {
        return matchWithResume.process(resumeId, object : MatchWithResumeObserver<CruncherResponse> {
            override fun success(matchedJobs: List<Job>): CruncherResponse {
                return JobsMatchedAnswer(
                        resultCode = ResultCodes.SUCCESS,
                        message = "Resume $resumeId matches ${matchedJobs.size} jobs",
                        jobs = mapOf("naiveBayes" to matchedJobs.map { it.toJobAnswer() })
                )
            }

            override fun resumeNotFound(resumeId: String): CruncherResponse {
                return CruncherResponse(
                        resultCode = ResultCodes.RESUME_NOT_FOUND,
                        message = "Resume $resumeId does not exist"
                )
            }

            override fun noMatches(resumeId: String): CruncherResponse {
                return JobsMatchedAnswer(
                        resultCode = ResultCodes.SUCCESS,
                        message = "Resume $resumeId as no matches",
                        jobs = mapOf("naiveBayes" to emptyList())
                )
            }

        })
    }

    @GetMapping("reindex")
    @ResponseBody
    fun reindex(): CruncherResponse {
        return reCrunchAllJobs.process(object :ReCrunchAllJobsObserver<CruncherResponse>{
            override fun onSuccess(numberScheduled: Int): CruncherResponse {
                return CruncherResponse(
                        resultCode = ResultCodes.SUCCESS,
                        message = "Going to reindex $numberScheduled jobs"
                )
            }

        })
    }
}

private fun Job.toJobAnswer(): JobAnswer {
    return JobAnswer(
            id,
            title,
            description,
            starRating
    )
}

class JobsMatchedAnswer(
        resultCode: ResultCodes,
        message: String,
        val jobs: Map<String, List<JobAnswer>>
) : CruncherResponse(
        resultCode,
        message)

data class JobAnswer(
        val id: String,
        val title: String,
        val description: String,
        val stars: Double
)