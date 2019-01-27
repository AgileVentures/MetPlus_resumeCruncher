package org.metplus.cruncher.canned.job

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.metplus.cruncher.job.Job
import org.metplus.cruncher.job.JobRepositoryFake
import org.metplus.cruncher.job.MatchWithResumeObserver
import org.metplus.cruncher.rating.CruncherMetaData
import org.metplus.cruncher.rating.MatcherStub
import org.metplus.cruncher.resume.ResumeRepositoryFake

internal class MatchWithResumeCannedTest {

    private lateinit var subject: MatchWithResumeCanned
    private lateinit var jobRepositoryFake: JobRepositoryFake

    @BeforeEach
    fun beforeEach() {
        jobRepositoryFake = JobRepositoryFake()
        subject = MatchWithResumeCanned(jobRepositoryFake)

        jobRepositoryFake.save(Job("1", title = "Some title", description = "some description", titleMetaData = mutableMapOf(), descriptionMetaData = mutableMapOf()))
        jobRepositoryFake.save(Job("2", title = "Some title", description = "some description", titleMetaData = mutableMapOf(), descriptionMetaData = mutableMapOf()))
        jobRepositoryFake.save(Job("3", title = "Some title", description = "some description", titleMetaData = mutableMapOf(), descriptionMetaData = mutableMapOf()))
        jobRepositoryFake.save(Job("4", title = "Some title", description = "some description", titleMetaData = mutableMapOf(), descriptionMetaData = mutableMapOf()))
        jobRepositoryFake.save(Job("5", title = "Some title", description = "some description", titleMetaData = mutableMapOf(), descriptionMetaData = mutableMapOf()))
        jobRepositoryFake.save(Job("6", title = "Some title", description = "some description", titleMetaData = mutableMapOf(), descriptionMetaData = mutableMapOf()))
        jobRepositoryFake.save(Job("7", title = "Some title", description = "some description", titleMetaData = mutableMapOf(), descriptionMetaData = mutableMapOf()))
        jobRepositoryFake.save(Job("8", title = "Some title", description = "some description", titleMetaData = mutableMapOf(), descriptionMetaData = mutableMapOf()))
        jobRepositoryFake.save(Job("9", title = "Some title", description = "some description", titleMetaData = mutableMapOf(), descriptionMetaData = mutableMapOf()))
        jobRepositoryFake.save(Job("10", title = "Some title", description = "some description", titleMetaData = mutableMapOf(), descriptionMetaData = mutableMapOf()))
        jobRepositoryFake.save(Job("11", title = "Some title", description = "some description", titleMetaData = mutableMapOf(), descriptionMetaData = mutableMapOf()))
        jobRepositoryFake.save(Job("15", title = "Some title", description = "some description", titleMetaData = mutableMapOf(), descriptionMetaData = mutableMapOf()))
    }

    @Test
    fun `when resume id is between 1 and 10, it return the first 10 jobs with amount of stars pre defined`() {
        var wasCalledWith = emptyList<Job>()
        subject.process("1", object : MatchWithResumeObserver<Boolean> {
            override fun success(matchedJobs: List<Job>): Boolean {
                wasCalledWith = matchedJobs
                return false
            }

            override fun noMatches(resumeId: String): Boolean {
                Assertions.fail("Should have called 'success' method")
                return false
            }

            override fun resumeNotFound(resumeId: String): Boolean {
                Assertions.fail("Should have called 'success' method")
                return true
            }
        })

        assertThat(wasCalledWith).hasSize(10)
        assertThat(wasCalledWith.first().starRating).isEqualTo(1.8)
        assertThat(wasCalledWith[1].starRating).isEqualTo(4.1)
        assertThat(wasCalledWith[2].starRating).isEqualTo(2.6)
        assertThat(wasCalledWith[3].starRating).isEqualTo(4.9)
        assertThat(wasCalledWith[4].starRating).isEqualTo(3.2)
        assertThat(wasCalledWith[5].starRating).isEqualTo(1.8)
        assertThat(wasCalledWith[6].starRating).isEqualTo(4.1)
        assertThat(wasCalledWith[7].starRating).isEqualTo(2.6)
        assertThat(wasCalledWith[8].starRating).isEqualTo(4.9)
        assertThat(wasCalledWith[9].starRating).isEqualTo(3.2)
    }

    @Test
    fun `when resume id is bigger than 10 and not multiple of 5, it return the amount of stars pre defined`() {
        var wasCalledWith = emptyList<Job>()
        subject.process("11", object : MatchWithResumeObserver<Boolean> {
            override fun success(matchedJobs: List<Job>): Boolean {
                wasCalledWith = matchedJobs
                return false
            }

            override fun noMatches(resumeId: String): Boolean {
                Assertions.fail("Should have called 'success' method")
                return false
            }

            override fun resumeNotFound(resumeId: String): Boolean {
                Assertions.fail("Should have called 'success' method")
                return true
            }
        })


        assertThat(wasCalledWith).hasSize(4)
        assertThat(wasCalledWith.first().starRating).isEqualTo(1.8)
        assertThat(wasCalledWith[1].starRating).isEqualTo(4.1)
        assertThat(wasCalledWith[2].starRating).isEqualTo(2.6)
        assertThat(wasCalledWith[3].starRating).isEqualTo(4.9)
    }

    @Test
    fun `when resume id is between bigger than 10 and multiple of 5, it calls noMatches`() {
        var wasCalledWith = ""
        subject.process("15", object : MatchWithResumeObserver<Boolean> {
            override fun success(matchedJobs: List<Job>): Boolean {
                Assertions.fail("Should have called 'noMatches' method")
                return false
            }

            override fun noMatches(resumeId: String): Boolean {
                wasCalledWith = resumeId
                return false
            }

            override fun resumeNotFound(resumeId: String): Boolean {
                Assertions.fail("Should have called 'noMatches' method")
                return true
            }
        })

        assertThat(wasCalledWith).isEqualTo("15")
    }
    @Test
    fun `when resume id is not a number, it calls resumeNotFound`() {
        var wasCalledWith = ""
        subject.process("bananas", object : MatchWithResumeObserver<Boolean> {
            override fun success(matchedJobs: List<Job>): Boolean {
                Assertions.fail("Should have called 'resumeNotFound' method")
                return false
            }

            override fun noMatches(resumeId: String): Boolean {
                Assertions.fail("Should have called 'resumeNotFound' method")
                return false
            }

            override fun resumeNotFound(resumeId: String): Boolean {
                wasCalledWith = resumeId
                return true
            }
        })

        assertThat(wasCalledWith).isEqualTo("bananas")
    }
}