package org.metplus.cruncher.canned.resume

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.metplus.cruncher.job.Job
import org.metplus.cruncher.job.MatchWithResumeObserver
import org.metplus.cruncher.rating.CruncherMetaData
import org.metplus.cruncher.resume.MatchWithJobObserver
import org.metplus.cruncher.resume.Resume
import org.metplus.cruncher.resume.ResumeRepositoryFake

internal class MatchWithJobCannedTest {

    private lateinit var subject: MatchWithJobCanned
    private lateinit var resumeRepositoryFake: ResumeRepositoryFake

    @BeforeEach
    fun beforeEach() {
        resumeRepositoryFake = ResumeRepositoryFake()
        subject = MatchWithJobCanned(resumeRepositoryFake)

        resumeRepositoryFake.save(Resume("some-filename", "1", "pdf", CruncherMetaData(mutableMapOf())))
        resumeRepositoryFake.save(Resume("some-filename", "2", "pdf", CruncherMetaData(mutableMapOf())))
        resumeRepositoryFake.save(Resume("some-filename", "3", "pdf", CruncherMetaData(mutableMapOf())))
        resumeRepositoryFake.save(Resume("some-filename", "4", "pdf", CruncherMetaData(mutableMapOf())))
        resumeRepositoryFake.save(Resume("some-filename", "5", "pdf", CruncherMetaData(mutableMapOf())))
        resumeRepositoryFake.save(Resume("some-filename", "6", "pdf", CruncherMetaData(mutableMapOf())))
        resumeRepositoryFake.save(Resume("some-filename", "7", "pdf", CruncherMetaData(mutableMapOf())))
        resumeRepositoryFake.save(Resume("some-filename", "8", "pdf", CruncherMetaData(mutableMapOf())))
        resumeRepositoryFake.save(Resume("some-filename", "9", "pdf", CruncherMetaData(mutableMapOf())))
        resumeRepositoryFake.save(Resume("some-filename", "10", "pdf", CruncherMetaData(mutableMapOf())))
        resumeRepositoryFake.save(Resume("some-filename", "11", "pdf", CruncherMetaData(mutableMapOf())))
        resumeRepositoryFake.save(Resume("some-filename", "15", "pdf", CruncherMetaData(mutableMapOf())))
    }

    @Test
    fun `when job id is between 1 and 10, it return the first 10 resumes with amount of stars pre defined`() {
        var wasCalledWith = emptyList<Resume>()
        subject.process("1", object : MatchWithJobObserver<Boolean> {
            override fun jobNotFound(jobId: String): Boolean {
                Assertions.fail("Should have called 'success' method")
                return true
            }

            override fun noMatchFound(jobId: String): Boolean {
                Assertions.fail("Should have called 'success' method")
                return false
            }

            override fun success(matchedResumes: List<Resume>): Boolean {
                wasCalledWith = matchedResumes
                return false
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
    fun `when job id is bigger than 10 and not multiple of 5, it return the amount of stars pre defined`() {
        var wasCalledWith = emptyList<Resume>()
        subject.process("11", object : MatchWithJobObserver<Boolean> {
            override fun jobNotFound(jobId: String): Boolean {
                Assertions.fail("Should have called 'success' method")
                return true
            }

            override fun noMatchFound(jobId: String): Boolean {
                Assertions.fail("Should have called 'success' method")
                return false
            }

            override fun success(matchedResumes: List<Resume>): Boolean {
                wasCalledWith = matchedResumes
                return false
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
        subject.process("15", object : MatchWithJobObserver<Boolean> {
            override fun jobNotFound(jobId: String): Boolean {
                Assertions.fail("Should have called 'noMatches' method")
                return true
            }

            override fun noMatchFound(jobId: String): Boolean {
                wasCalledWith = jobId
                return false
            }

            override fun success(matchedResumes: List<Resume>): Boolean {
                Assertions.fail("Should have called 'noMatches' method")
                return false
            }
        })

        assertThat(wasCalledWith).isEqualTo("15")
    }

    @Test
    fun `when resume id is not a number, it calls resumeNotFound`() {
        var wasCalledWith = ""
        subject.process("bananas", object : MatchWithJobObserver<Boolean> {
            override fun jobNotFound(jobId: String): Boolean {
                wasCalledWith = jobId
                return true
            }

            override fun noMatchFound(jobId: String): Boolean {
                Assertions.fail("Should have called 'resumeNotFound' method")
                return false
            }

            override fun success(matchedResumes: List<Resume>): Boolean {
                Assertions.fail("Should have called 'resumeNotFound' method")
                return false
            }
        })

        assertThat(wasCalledWith).isEqualTo("bananas")
    }
}