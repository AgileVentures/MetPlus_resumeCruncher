package org.metplus.cruncher.resume

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.metplus.cruncher.job.Job
import org.metplus.cruncher.job.JobRepositoryFake
import org.metplus.cruncher.rating.MatcherStub
import org.metplus.cruncher.rating.emptyMetaData

internal class MatchWithJobTest {
    private lateinit var matchWithJob: MatchWithJob
    private lateinit var jobRepositoryFake: JobRepositoryFake
    private lateinit var resumeRepositoryFake: ResumeRepositoryFake
    private lateinit var matcherStub: MatcherStub
    @BeforeEach
    fun beforeEach() {
        jobRepositoryFake = JobRepositoryFake()
        resumeRepositoryFake = ResumeRepositoryFake()
        matcherStub = MatcherStub()
        matchWithJob = MatchWithJob(resumeRepositoryFake, jobRepositoryFake, matcherStub)
    }

    @Test
    fun `when job does not exist, it calls jobNotFound callback with the job id provided`() {
        var wasCalledWith = ""
        matchWithJob.process("not-found", observer = object : MatchWithJobObserver<Boolean> {
            override fun success(matchedResumes: List<Resume>): Boolean {
                fail("Should have called jobNotFound")
                return false
            }

            override fun noMatchFound(jobId: String): Boolean {
                fail("Should have called jobNotFound")
                return false
            }

            override fun jobNotFound(jobId: String): Boolean {
                wasCalledWith = jobId
                return true
            }
        })

        assertThat(wasCalledWith).isEqualTo("not-found")
    }

    @Test
    fun `when job exists but have no match, it calls noMatchFound callback with the job id provided`() {
        var wasCalledWith = ""
        jobRepositoryFake.save(Job("some-job-id", "some title", "some description", emptyMetaData(), emptyMetaData()))

        matchWithJob.process("some-job-id", observer = object : MatchWithJobObserver<Boolean> {
            override fun success(matchedResumes: List<Resume>): Boolean {
                fail("Should have called noMatchFound")
                return false
            }

            override fun noMatchFound(jobId: String): Boolean {
                wasCalledWith = jobId
                return true
            }

            override fun jobNotFound(jobId: String): Boolean {
                fail("Should have called noMatchFound")
                return false
            }
        })

        assertThat(wasCalledWith).isEqualTo("some-job-id")
    }

    @Test
    fun `when job exists and matches two resumes, it calls success callback with the two matched resumes`() {
        var wasCalledWith = emptyList<Resume>()
        jobRepositoryFake.save(Job("some-job-id", "some title", "some description", emptyMetaData(), emptyMetaData()))
        val resume1 = Resume("some-file", "some-user-id", "pdf", emptyMetaData())
        resumeRepositoryFake.save(resume1)
        resumeRepositoryFake.save(Resume("some-other-file", "yet-other-user-id", "pdf", emptyMetaData()))
        val resume2 = Resume("some-other-file", "some-other-user-id", "pdf", emptyMetaData())
        resumeRepositoryFake.save(resume2)

        matcherStub.matchInverseReturnValue = listOf(resume2.copy(starRating = 4.1), resume1.copy(starRating = 0.1))

        matchWithJob.process("some-job-id", observer = object : MatchWithJobObserver<Boolean> {
            override fun success(matchedResumes: List<Resume>): Boolean {
                wasCalledWith = matchedResumes
                return true
            }

            override fun noMatchFound(jobId: String): Boolean {
                fail("Should have called success")
                return false
            }

            override fun jobNotFound(jobId: String): Boolean {
                fail("Should have called success")
                return false
            }
        })

        assertThat(wasCalledWith).isEqualTo(listOf(resume2.copy(starRating = 4.1), resume1.copy(starRating = 0.1)))
    }
}