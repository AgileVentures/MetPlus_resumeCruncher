package org.metplus.cruncher.canned.resume

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.metplus.cruncher.rating.MatcherList
import org.metplus.cruncher.rating.MatcherStub
import org.metplus.cruncher.resume.MatchWithJobObserver
import org.metplus.cruncher.resume.Resume
import org.metplus.cruncher.resume.ResumeRepositoryFake

internal class MatchWithJobCannedTest {

    private lateinit var subject: MatchWithJobCanned
    private lateinit var resumeRepositoryFake: ResumeRepositoryFake

    @BeforeEach
    fun beforeEach() {
        resumeRepositoryFake = ResumeRepositoryFake()
        subject = MatchWithJobCanned(resumeRepositoryFake, matcherList = MatcherList(listOf(MatcherStub())))

        resumeRepositoryFake.save(Resume("some-filename", "1", "pdf", mapOf()))
        resumeRepositoryFake.save(Resume("some-filename", "2", "pdf", mapOf()))
        resumeRepositoryFake.save(Resume("some-filename", "3", "pdf", mapOf()))
        resumeRepositoryFake.save(Resume("some-filename", "4", "pdf", mapOf()))
        resumeRepositoryFake.save(Resume("some-filename", "5", "pdf", mapOf()))
        resumeRepositoryFake.save(Resume("some-filename", "6", "pdf", mapOf()))
        resumeRepositoryFake.save(Resume("some-filename", "7", "pdf", mapOf()))
        resumeRepositoryFake.save(Resume("some-filename", "8", "pdf", mapOf()))
        resumeRepositoryFake.save(Resume("some-filename", "9", "pdf", mapOf()))
        resumeRepositoryFake.save(Resume("some-filename", "10", "pdf", mapOf()))
        resumeRepositoryFake.save(Resume("some-filename", "11", "pdf", mapOf()))
        resumeRepositoryFake.save(Resume("some-filename", "15", "pdf", mapOf()))
    }

    @Test
    fun `when job id is between 1 and 10, it return the first 10 resumes with amount of stars pre defined`() {
        var wasCalledWith = mapOf<String, List<Resume>>()
        subject.process("1", object : MatchWithJobObserver<Boolean> {
            override fun jobNotFound(jobId: String): Boolean {
                Assertions.fail("Should have called 'success' method")
                return true
            }

            override fun noMatchFound(jobId: String, matchers: Map<String, List<Resume>>): Boolean {
                Assertions.fail("Should have called 'success' method")
                return false
            }

            override fun success(matchedResumes: Map<String, List<Resume>>): Boolean {
                wasCalledWith = matchedResumes
                return false
            }
        })

        val listOfResumes = wasCalledWith["matcher-1"]
        assertThat(listOfResumes).isNotNull
        assertThat(listOfResumes!!).hasSize(10)
        assertThat(listOfResumes.first().starRating).isEqualTo(1.8)
        assertThat(listOfResumes[1].starRating).isEqualTo(4.1)
        assertThat(listOfResumes[2].starRating).isEqualTo(2.6)
        assertThat(listOfResumes[3].starRating).isEqualTo(4.9)
        assertThat(listOfResumes[4].starRating).isEqualTo(3.2)
        assertThat(listOfResumes[5].starRating).isEqualTo(1.8)
        assertThat(listOfResumes[6].starRating).isEqualTo(4.1)
        assertThat(listOfResumes[7].starRating).isEqualTo(2.6)
        assertThat(listOfResumes[8].starRating).isEqualTo(4.9)
        assertThat(listOfResumes[9].starRating).isEqualTo(3.2)
    }

    @Test
    fun `when job id is bigger than 10 and not multiple of 5, it return the amount of stars pre defined`() {
        var wasCalledWith = mapOf<String, List<Resume>>()
        subject.process("11", object : MatchWithJobObserver<Boolean> {
            override fun jobNotFound(jobId: String): Boolean {
                Assertions.fail("Should have called 'success' method")
                return true
            }

            override fun noMatchFound(jobId: String, matchers: Map<String, List<Resume>>): Boolean {
                Assertions.fail("Should have called 'success' method")
                return false
            }

            override fun success(matchedResumes: Map<String, List<Resume>>): Boolean {
                wasCalledWith = matchedResumes
                return false
            }
        })

        val listOfResumes = wasCalledWith["matcher-1"]
        assertThat(listOfResumes!!).hasSize(4)
        assertThat(listOfResumes.first().starRating).isEqualTo(1.8)
        assertThat(listOfResumes[1].starRating).isEqualTo(4.1)
        assertThat(listOfResumes[2].starRating).isEqualTo(2.6)
        assertThat(listOfResumes[3].starRating).isEqualTo(4.9)
    }

    @Test
    fun `when resume id is between bigger than 10 and multiple of 5, it calls noMatches`() {
        var wasCalledWith = ""
        subject.process("15", object : MatchWithJobObserver<Boolean> {
            override fun jobNotFound(jobId: String): Boolean {
                Assertions.fail("Should have called 'noMatches' method")
                return true
            }

            override fun noMatchFound(jobId: String, matchers: Map<String, List<Resume>>): Boolean {
                wasCalledWith = jobId
                return false
            }

            override fun success(matchedResumes: Map<String, List<Resume>>): Boolean {
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

            override fun noMatchFound(jobId: String, matchers: Map<String, List<Resume>>): Boolean {
                Assertions.fail("Should have called 'resumeNotFound' method")
                return false
            }

            override fun success(matchedResumes: Map<String, List<Resume>>): Boolean {
                Assertions.fail("Should have called 'resumeNotFound' method")
                return false
            }
        })

        assertThat(wasCalledWith).isEqualTo("bananas")
    }
}