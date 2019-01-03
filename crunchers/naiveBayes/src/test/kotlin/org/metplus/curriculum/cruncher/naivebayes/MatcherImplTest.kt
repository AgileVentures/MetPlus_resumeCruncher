package org.metplus.curriculum.cruncher.naivebayes


import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.data.Offset
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.metplus.cruncher.job.Job
import org.metplus.cruncher.rating.CruncherMetaData
import org.metplus.cruncher.resume.Resume

open class Base {
    protected lateinit var matcher: MatcherImpl
    protected lateinit var resumeCategoryOneAndTwo: Resume
    protected lateinit var resumeCategoryOne: Resume
    protected lateinit var jobTitleCat1DescCat2: Job
    protected lateinit var jobTitleCat3DescCat4: Job

    @BeforeEach
    fun before() {
        matcher = MatcherImpl()


        resumeCategoryOne = Resume(
                "some-filename",
                "some-other-user-id",
                "pdf",
                CruncherMetaData(mutableMapOf("cat_1_job" to 1.0)))

        resumeCategoryOneAndTwo = Resume(
                "filename",
                "some-user-id",
                "pdf",
                CruncherMetaData(mutableMapOf("cat_1_job" to 1.0,
                        "cat_2_job" to .9)))

        var titleData = CruncherMetaData(mutableMapOf("cat_1_job" to 1.0))
        var descriptionData = CruncherMetaData(mutableMapOf("cat_2_job" to 1.0))
        jobTitleCat1DescCat2 = Job(
                "cat-1 and cat-2",
                "some title",
                "some description",
                titleData,
                descriptionData)

        titleData = CruncherMetaData(mutableMapOf("cat_3_job" to 1.0))
        descriptionData = CruncherMetaData(mutableMapOf("cat_4_job" to 1.0))
        jobTitleCat3DescCat4 = Job(
                "cat-3 and cat-4",
                "some other title",
                "some other description",
                titleData,
                descriptionData
        )
    }
}

class MatchAgainstResume : Base() {
    @Test
    fun `when matching a resume with no categories, it return a empty list of jobs`() {
        val resume = Resume("some_file.pdf", "user-id", "pdf", CruncherMetaData(mutableMapOf()))
        val allJobs = listOf(
                Job("1",
                        "some title",
                        "some description",
                        CruncherMetaData(mutableMapOf("some-title-feature" to 1.0)),
                        CruncherMetaData(mutableMapOf("some-desc-feature" to 1.0))),
                Job("2",
                        "some other title",
                        "some other description",
                        CruncherMetaData(mutableMapOf("some-other-title-feature" to 1.0)),
                        CruncherMetaData(mutableMapOf("some-other-desc-feature" to 1.0))))
        assertThat(matcher.match(resume, allJobs)).hasSize(0)
    }

    @Test
    fun `when resume as two categories, it matches with 3_87 star rating`() {
        val allJobs = ArrayList<Job>()
        allJobs.add(jobTitleCat1DescCat2)


        val expectedResults = ArrayList<Job>()
        expectedResults.add(jobTitleCat1DescCat2)

        val results = matcher.match(resumeCategoryOneAndTwo, allJobs)

        assertThat(results).hasSize(1)
        assertThat(results.first()).isEqualToIgnoringGivenFields(jobTitleCat1DescCat2.copy(), "starRating")

        assertThat(results.first().starRating).isEqualTo(3.87, Offset.offset(0.001))
    }

    @Test
    fun `when matching a resume with jobs that do not match any category, it returns an empty list`() {
        val allJobs = ArrayList<Job>()
        allJobs.add(jobTitleCat3DescCat4)

        assertThat(matcher.match(resumeCategoryOneAndTwo, allJobs)).isEmpty()
    }

    @Test
    fun `when resume as matching category in sixth position, it returns an empty list`() {
        val resume = Resume(
                "filename",
                "some-user-id",
                "pdf",
                CruncherMetaData(mutableMapOf(
                        "cat_1_job" to 1.0,
                        "cat_2_job" to .9,
                        "cat_5_job" to .8,
                        "cat_6_job" to .7,
                        "cat_7_job" to .6,
                        "cat_4_job" to .5)))

        val allJobs = ArrayList<Job>()
        allJobs.add(jobTitleCat3DescCat4)

        assertThat(matcher.match(resume, allJobs)).isEmpty()
    }

    @Test
    fun `when resume with one category matches with job description category in the sixth position it should not return as a match`() {
        val titleData = CruncherMetaData(mutableMapOf(
                "cat_2_job" to 1.0,
                "cat_3_job" to 0.9))
        val descriptionData = CruncherMetaData(mutableMapOf(
                "cat_4_job" to 1.0,
                "cat_5_job" to .9,
                "cat_6_job" to .8,
                "cat_1_job" to .7))
        val job = Job(
                "cat-3 and cat-4",
                "some other title",
                "some other description",
                titleData,
                descriptionData
        )

        val allJobs = listOf(job)
        assertThat(matcher.match(resumeCategoryOne, allJobs)).isEmpty()
    }

    @Test
    fun `when resume has 1 category and matches the third Job Title category it returns no match`() {
        val titleData = CruncherMetaData(mutableMapOf(
                "cat_2_job" to 1.0,
                "cat_3_job" to 0.9,
                "cat_1_job" to .7))
        val descriptionData = CruncherMetaData(mutableMapOf(
                "cat_4_job" to 1.0,
                "cat_5_job" to .9,
                "cat_6_job" to .8))
        val job = Job(
                "cat-3 and cat-4",
                "some other title",
                "some other description",
                titleData,
                descriptionData
        )

        val allJobs = listOf(job)
        assertThat(matcher.match(resumeCategoryOne, allJobs)).isEmpty()
    }

    @Test
    fun `when resume as the same category twice with different suffix and Job Title matches, it matches with 285 star rating`() {
        val titleData = CruncherMetaData(mutableMapOf(
                "cat_1_job" to .7))
        val descriptionData = CruncherMetaData(mutableMapOf(
                "cat_4_job" to 1.0))
        val job = Job(
                "cat-3 and cat-4",
                "some other title",
                "some other description",
                titleData,
                descriptionData
        )

        val expectedResults = ArrayList<Job>()
        expectedResults.add(job)

        val allJobs = listOf(job)

        val resume = Resume(
                "some-filename",
                "some-other-user-id",
                "pdf",
                CruncherMetaData(mutableMapOf(
                        "cat_1_resume" to 1.0,
                        "cat_1_job" to .9
                )))

        val results = matcher.match(resume, allJobs)
        assertThat(results).hasSize(1)
        assertThat(results.first()).isEqualToIgnoringGivenFields(job.copy(), "starRating")

        assertThat(results.first().starRating).isEqualTo(2.58, Offset.offset(0.001))
    }

}

class MatchAgainstJob : Base() {
    @Test
    fun `when job as no categories, it returns empty list`() {
        val job = Job(
                "some-id",
                "some title",
                "some description",
                CruncherMetaData(mutableMapOf()),
                CruncherMetaData(mutableMapOf())
        )

        assertThat(matcher.matchInverse(job, listOf(resumeCategoryOne))).isEmpty()
    }

    @Test
    fun `when Job as Category Two and Resume as it too, it returns resume with 258 star rating`() {
        val expectedResults = ArrayList<Resume>()
        expectedResults.add(resumeCategoryOne)

        val results = matcher.matchInverse(jobTitleCat1DescCat2, listOf(resumeCategoryOne))

        assertThat(results).hasSize(1)
        assertThat(results.first()).isEqualToIgnoringGivenFields(resumeCategoryOne.copy(), "starRating")
        assertThat(results.first().starRating).isEqualTo(2.58, Offset.offset(0.01))
    }

    @Test
    fun `when job as category two and resume doesn't, it does not return any resume`() {
        assertThat(matcher.match(resumeCategoryOne, listOf(jobTitleCat3DescCat4))).isEmpty()
    }

    @Test
    fun `when resume with 6 categories and Job that matches sixth, it returns no resume`() {
        val resume = Resume(
                "some-filename",
                "some-other-user-id",
                "pdf",
                CruncherMetaData(mutableMapOf(
                        "cat_1_job" to 1.0,
                        "cat_2_job" to .9,
                        "cat_5_job" to .8,
                        "cat_6_job" to .8,
                        "cat_7_job" to .7,
                        "cat_3_job" to .6
                )))
        assertThat(matcher.matchInverse(jobTitleCat3DescCat4, listOf(resume))).isEmpty()
    }

    @Test
    fun `when job category 1 is the 4th in the description and 1rst in the resume, it returns no resume`() {
        val job = Job(
                "job-id",
                "job title",
                "job description",
                CruncherMetaData(mutableMapOf(
                        "cat_2_job" to 1.0,
                        "cat_3_job" to 0.9
                )),
                CruncherMetaData(mutableMapOf(
                        "cat_4_job" to 1.0,
                        "cat_5_job" to 0.9,
                        "cat_6_job" to 0.8,
                        "cat_1_job" to 0.7
                ))
        )

        assertThat(matcher.matchInverse(job, listOf(resumeCategoryOne))).isEmpty()
    }

    @Test
    fun `when job as category 1 in the 3rd position of the title and resume is category 1, it returns no resume`() {
        val job = Job(
                "job-id",
                "job title",
                "job description",
                CruncherMetaData(mutableMapOf(
                        "cat_2_job" to 1.0,
                        "cat_3_job" to 0.9,
                        "cat_1_job" to 0.7
                )),
                CruncherMetaData(mutableMapOf(
                        "cat_4_job" to 1.0,
                        "cat_5_job" to 0.9,
                        "cat_6_job" to 0.8
                ))
        )

        assertThat(matcher.matchInverse(job, listOf(resumeCategoryOne))).isEmpty()
    }

    @Test
    fun `when job as category 1 in 3rd position but previous 2 are the same category and resume is category 1, it returns the resume with star rating of 120`() {
        val job = Job(
                "job-id",
                "job title",
                "job description",
                CruncherMetaData(mutableMapOf(
                        "cat_2_job" to 1.0,
                        "cat_2_resume" to 0.9,
                        "cat_1_job" to 0.7
                )),
                CruncherMetaData(mutableMapOf(
                        "cat_4_job" to 1.0,
                        "cat_5_job" to 0.9,
                        "cat_6_job" to 0.8
                ))
        )

        val expectedResults = ArrayList<Resume>()
        expectedResults.add(resumeCategoryOne)

        val results = matcher.matchInverse(job, listOf(resumeCategoryOne))
        assertThat(results).hasSize(1)
        assertThat(results.first()).isEqualToIgnoringGivenFields(resumeCategoryOne.copy(), "starRating")
        assertThat(results.first().starRating).isEqualTo(1.2, Offset.offset(.01))
    }

    @Test
    fun `when job as category 1 in Title and Description and resume as category 1, it returns resume with star rating 120`() {
        val job = Job(
                "job-id",
                "job title",
                "job description",
                CruncherMetaData(mutableMapOf(
                        "cat_2_job" to 1.0,
                        "cat_1_job" to 0.9
                )),
                CruncherMetaData(mutableMapOf(
                        "cat_4_job" to 1.0,
                        "cat_1_resume" to 0.9,
                        "cat_5_job" to 0.8
                ))
        )

        val results = matcher.matchInverse(job, listOf(resumeCategoryOne))
        assertThat(results).hasSize(1)
        assertThat(results.first()).isEqualToIgnoringGivenFields(resumeCategoryOne.copy(), "starRating")
        assertThat(results.first().starRating).isEqualTo(1.2, Offset.offset(.01))
    }
}

class MatchSimilarity : Base() {
    @Test
    fun `when resume as no categories, it returns 0`() {
        val resume = Resume(
                "filename",
                "some-user-id",
                "pdf",
                CruncherMetaData(mutableMapOf()))
        assertThat(matcher.similarityRating(resume, jobTitleCat3DescCat4)).isEqualTo(.0)
    }

    @Test
    fun `when job as no categories, it returns 0`() {
        val job = Job(
                "job-id",
                "job title",
                "job description",
                CruncherMetaData(mutableMapOf()),
                CruncherMetaData(mutableMapOf())
        )
        assertThat(matcher.similarityRating(resumeCategoryOne, job)).isEqualTo(.0)
    }

    @Test
    fun `when job with category 1 in title and 2 in description and resume with category 1 and 2, it returns 387`() {
        assertThat(matcher.similarityRating(resumeCategoryOneAndTwo, jobTitleCat1DescCat2))
                .isEqualTo(3.87, Offset.offset(0.01))
    }
}

class CheckStarRating : Base() {
    @Test
    fun `when 2 lists have the categories in the same order, it returns 5 stars`() {
        val element = ArrayList<String>()
        element.add("cat_1")
        element.add("cat_2")
        element.add("cat_3")
        element.add("cat_4")
        element.add("cat_5")
        assertThat(matcher.calculateStarRating(element, element))
                .isEqualTo(5.0, Offset.offset(.01))
    }


    @Test
    fun `when the lists have 1 category and is different, it return 0 stars`() {
        val base = ArrayList<String>()
        base.add("cat_1")
        val compare = ArrayList<String>()
        compare.add("cat_2")
        assertThat(matcher.calculateStarRating(base, compare))
                .isEqualTo(.0, Offset.offset(0.01))
    }

    @Test
    fun `when first list as category 1 and second as category 2 and 1, it returns 194 stars`() {
        val base = ArrayList<String>()
        base.add("cat_1")
        val compare = ArrayList<String>()
        compare.add("cat_2")
        compare.add("cat_1")
        assertThat(matcher.calculateStarRating(base, compare))
                .isEqualTo(1.94, Offset.offset(0.01))
    }

    @Test
    fun `when first list as categories 1,2 and second 3,4,2,1, it returns 242 stars`() {
        val base = ArrayList<String>()
        base.add("cat_1")
        base.add("cat_2")
        val compare = ArrayList<String>()
        compare.add("cat_3")
        compare.add("cat_4")
        compare.add("cat_2")
        compare.add("cat_1")
        assertThat(matcher.calculateStarRating(base, compare))
                .isEqualTo(2.42, Offset.offset(0.01))
    }
}