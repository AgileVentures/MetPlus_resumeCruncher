package org.metplus.cruncher.rating

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.metplus.cruncher.job.Job
import org.metplus.cruncher.job.JobRepositoryFake
import org.metplus.cruncher.resume.Resume
import org.metplus.cruncher.resume.ResumeRepositoryFake

internal class CompareResumeWithJobTest {
    private lateinit var compareResumeWithJob: CompareResumeWithJob
    private lateinit var jobRepositoryFake: JobRepositoryFake
    private lateinit var resumeRepositoryFake: ResumeRepositoryFake
    private lateinit var matcherStub: MatcherStub

    @BeforeEach
    fun beforeEach() {
        jobRepositoryFake = JobRepositoryFake()
        resumeRepositoryFake = ResumeRepositoryFake()
        matcherStub = MatcherStub()
        compareResumeWithJob = CompareResumeWithJob(
                jobRepositoryFake,
                resumeRepositoryFake,
                matcherStub)
    }

    @Test
    fun `when job cannot be found, it calls onJobNotFound callback`() {
        var wasCalledWith = ""
        compareResumeWithJob.process(
                "resume-id",
                "not-found",
                object : CompareResumeWithJobObserver<Boolean> {
                    override fun onSuccess(starsRating: Double): Boolean {
                        fail("Should have called onJobNotFound callback")
                        return false
                    }

                    override fun onResumeNotFound(resumeId: String): Boolean {
                        fail("Should have called onJobNotFound callback")
                        return false
                    }

                    override fun onJobNotFound(jobId: String): Boolean {
                        wasCalledWith = jobId
                        return true
                    }
                })
        assertThat(wasCalledWith).isEqualTo("not-found")
    }

    @Test
    fun `when job exist but resume cannot be found, it calls onResumeNotFound callback`() {
        var wasCalledWith = ""
        jobRepositoryFake.save(Job("job-id", "some title", "some description", mapOf(), mapOf()))
        compareResumeWithJob.process(
                "not-found",
                "job-id",
                object : CompareResumeWithJobObserver<Boolean> {
                    override fun onSuccess(starsRating: Double): Boolean {
                        fail("Should have called onResumeNotFound callback")
                        return false
                    }

                    override fun onResumeNotFound(resumeId: String): Boolean {
                        wasCalledWith = resumeId
                        return true
                    }

                    override fun onJobNotFound(jobId: String): Boolean {
                        fail("Should have called onResumeNotFound callback")
                        return false
                    }
                })
        assertThat(wasCalledWith).isEqualTo("not-found")
    }

    @Test
    fun `when job exist and resume found, it calls onSuccess callback with the amount of stars`() {
        var wasCalledWith = -10.0
        jobRepositoryFake.save(Job("job-id", "some title", "some description", mapOf(), mapOf()))
        resumeRepositoryFake.save(Resume("filename", "resume-id", "pdf", mapOf()))
        matcherStub.similarityRatingReturnValue = 1.5

        compareResumeWithJob.process(
                "resume-id",
                "job-id",
                object : CompareResumeWithJobObserver<Boolean> {
                    override fun onSuccess(starsRating: Double): Boolean {
                        wasCalledWith = starsRating
                        return true
                    }

                    override fun onResumeNotFound(resumeId: String): Boolean {
                        fail("Should have called onSuccess callback")
                        return false
                    }

                    override fun onJobNotFound(jobId: String): Boolean {
                        fail("Should have called onSuccess callback")
                        return false
                    }
                })
        assertThat(wasCalledWith).isEqualTo(1.5)
    }
}