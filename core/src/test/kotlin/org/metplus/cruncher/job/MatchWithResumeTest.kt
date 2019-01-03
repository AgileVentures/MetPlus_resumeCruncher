package org.metplus.cruncher.job

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.metplus.cruncher.rating.MatcherStub
import org.metplus.cruncher.rating.emptyMetaData
import org.metplus.cruncher.resume.Resume
import org.metplus.cruncher.resume.ResumeRepositoryFake

internal class MatchWithResumeTest {
    private lateinit var subject: MatchWithResume
    private lateinit var resumeRepositoryFake: ResumeRepositoryFake
    private lateinit var jobRepositoryFake: JobRepositoryFake
    private lateinit var matcher: MatcherStub

    @BeforeEach
    fun beforeEach() {
        resumeRepositoryFake = ResumeRepositoryFake()
        jobRepositoryFake = JobRepositoryFake()
        matcher = MatcherStub()
        subject = MatchWithResume(resumeRepositoryFake, jobRepositoryFake, matcher)
    }

    @Test
    fun `when resume cannot be found, it calls not found callback`() {
        var wasCalledWith = ""

        subject.process("999", object : MatchWithResumeObserver<Boolean> {
            override fun success(matchedJobs: List<Job>): Boolean {
                fail("Should have called 'resumeNotFound' method")
                return false
            }

            override fun noMatches(resumeId: String): Boolean {
                fail("Should have called 'resumeNotFound' method")
                return false
            }

            override fun resumeNotFound(resumeId: String): Boolean {
                wasCalledWith = resumeId
                return true
            }
        })

        assertThat(wasCalledWith).isEqualTo("999")
    }

    @Test
    fun `when resume does not match with any job, it calls no matches found callback`() {
        var wasCalledWith = ""
        resumeRepositoryFake.save(Resume(
                "filename",
                "user-id",
                "pdf",
                emptyMetaData()
        ))
        jobRepositoryFake.save(Job(
                "jobId",
                "job title",
                "job description",
                emptyMetaData(),
                emptyMetaData()
        ))

        matcher.matchReturnValue = emptyList()

        subject.process("user-id", object : MatchWithResumeObserver<Boolean> {
            override fun success(matchedJobs: List<Job>): Boolean {
                fail("Should have called 'noMatch' method")
                return true
            }

            override fun noMatches(resumeId: String): Boolean {
                wasCalledWith = resumeId
                return true
            }

            override fun resumeNotFound(resumeId: String): Boolean {
                fail("Should have called 'noMatch' method")
                return true
            }
        })

        assertThat(wasCalledWith).isEqualTo("user-id")
    }


    @Test
    fun `when resume does match with one job, it calls success callback with job`() {
        var wasCalledWith = listOf<Job>()
        resumeRepositoryFake.save(Resume(
                "filename",
                "user-id",
                "pdf",
                emptyMetaData()
        ))
        jobRepositoryFake.save(Job(
                "jobId",
                "job title",
                "job description",
                emptyMetaData(),
                emptyMetaData()
        ))
        val job = Job(
                "jobId1",
                "some job title",
                "some job description",
                emptyMetaData(),
                emptyMetaData()
        )
        jobRepositoryFake.save(job)
        jobRepositoryFake.save(Job(
                "jobId2",
                "some other job title",
                "some other job description",
                emptyMetaData(),
                emptyMetaData()
        ))

        matcher.matchReturnValue = listOf(job.copy(starRating = 3.1))

        subject.process("user-id", object : MatchWithResumeObserver<Boolean> {
            override fun success(matchedJobs: List<Job>): Boolean {
                wasCalledWith = matchedJobs
                return true
            }

            override fun noMatches(resumeId: String): Boolean {
                fail("Should have called 'success' method")
                return true
            }

            override fun resumeNotFound(resumeId: String): Boolean {
                fail("Should have called 'success' method")
                return true
            }
        })

        assertThat(wasCalledWith).isEqualTo(listOf(job.copy(starRating = 3.1)))
    }
}