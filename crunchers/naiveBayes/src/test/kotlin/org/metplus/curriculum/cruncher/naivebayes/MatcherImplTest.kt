package org.metplus.curriculum.cruncher.naivebayes


import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.metplus.cruncher.job.Job
import org.metplus.cruncher.job.JobsRepository
import org.metplus.cruncher.rating.CruncherMetaData
import org.metplus.cruncher.resume.Resume
import org.metplus.cruncher.resume.ResumeRepository

open class Base {

    protected lateinit var jobRespository: JobsRepository
    protected lateinit var resumeRespository: ResumeRepository

    protected lateinit var matcher: MatcherImpl
    protected lateinit var cruncher: CruncherImpl
    protected lateinit var resumeCategoryOneAndTwo: Resume
    protected lateinit var resumeCategoryOne: Resume
    protected lateinit var jobTitleCat1DescCat2: Job
    protected lateinit var jobTitleCat3DescCat4: Job

    @BeforeEach
    fun before() {
        cruncher = CruncherImpl()
        matcher = MatcherImpl()


//        val categories = ArrayList<String>()
//        categories.add("cat_1_job")
//        resumeCategoryOne = createResume(categories)
//
//        categories.add("cat_2_job")
        resumeCategoryOneAndTwo = Resume(
                "filename",
                "some-user-id",
                "pdf",
                CruncherMetaData(mutableMapOf("cat_1_job" to 1.0,
                        "cat_2_job" to .9)))

        val titleData = CruncherMetaData(mutableMapOf("cat_1_job" to 1.0))
        val descriptionData = CruncherMetaData(mutableMapOf("cat_2_job" to 1.0))
        jobTitleCat1DescCat2 = Job(
                "cat-1 and cat-2",
                "some title",
                "some description",
                titleData,
                descriptionData)
//
//        jobTitleCat3DescCat4 = Job()
//        titleData = NaiveBayesMetaData()
//        titleData.addCategory("cat_3_job", 1.0)
//        descriptionData = NaiveBayesMetaData()
//        descriptionData.addCategory("cat_4_job", 1.0)
//        setJobMetaData(jobTitleCat3DescCat4, titleData, descriptionData)
    }
//    protected fun setJobMetaData(job: Job,
//                                 titleMetaData: NaiveBayesMetaData,
//                                 descriptionMetaData: NaiveBayesMetaData) {
//        val allDescriptionMetaData = HashMap<String, MetaData>()
//        val allTitleMetaData = HashMap<String, MetaData>()
//        allTitleMetaData[CruncherImpl.CRUNCHER_NAME] = titleMetaData
//        allDescriptionMetaData[CruncherImpl.CRUNCHER_NAME] = descriptionMetaData
//        val titleData = DocumentWithMetaData()
//        titleData.setMetaData(allTitleMetaData)
//        job.setTitleMetaData(titleData)
//        val descriptionData = DocumentWithMetaData()
//        descriptionData.setMetaData(allDescriptionMetaData)
//        job.setDescriptionMetaData(descriptionData)
//    }
//
//    protected fun createResume(categories: List<String>): Resume {
//        val resumeMetaData = NaiveBayesMetaData()
//        var probability = 1.0
//        for (category in categories) {
//            resumeMetaData.addCategory(category, probability)
//            probability -= .1
//        }
//        val resumeCruncherMetaData = HashMap<String, MetaData>()
//        resumeCruncherMetaData[cruncher.getCruncherName()] = resumeMetaData
//        val resume = Resume()
//        resume.setMetaData(resumeCruncherMetaData)
//        return resume
//    }
}

class MatchAgainstResume : Base() {
    @Test
    fun `when matching a resume with categories, it return a empty list of jobs`() {
        val resume = Resume("some_file.pdf", "user-id", "pdf", CruncherMetaData(mutableMapOf()))
        val allJobs = listOf<Job>(
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
        assertThat(results.first()).isEqualTo(jobTitleCat1DescCat2.copy())

//        assertEquals(3.87, results.get(0).getStarRating(), 0.01)
    }
//
//    @Test
//    fun resumeWithTwoCategory_shouldMatchNoJob() {
//
//        val allJobs = ArrayList<Job>()
//        allJobs.add(jobTitleCat3DescCat4)
//        `when`(jobRespository.findAll()).thenReturn(allJobs)
//
//        val expectedResults = ArrayList<Job>()
//
//        assertEquals(expectedResults, matcher.match(resumeCategoryOneAndTwo))
//    }
//
//    @Test
//    fun resumeWithSixCategoryAndOnlySixMatch_shouldMatchNoJob() {
//        val resumeCategories = ArrayList<String>()
//        resumeCategories.add("cat_1_job")
//        resumeCategories.add("cat_2_job")
//        resumeCategories.add("cat_5_job")
//        resumeCategories.add("cat_6_job")
//        resumeCategories.add("cat_7_job")
//        resumeCategories.add("cat_3_job")
//        val resume = createResume(resumeCategories)
//
//        val allJobs = ArrayList<Job>()
//        allJobs.add(jobTitleCat3DescCat4)
//        `when`(jobRespository.findAll()).thenReturn(allJobs)
//
//        val expectedResults = ArrayList<Job>()
//
//        assertEquals(expectedResults, matcher.match(resume))
//    }
//
//    @Test
//    fun resumeWith1CategoryAndJobCategoryInDescriptionSixMatch_shouldMatchNoJob() {
//        val job1 = Job()
//        val titleData = NaiveBayesMetaData()
//        titleData.addCategory("cat_2_job", 1.0)
//        titleData.addCategory("cat_3_job", .9)
//        val descriptionData = NaiveBayesMetaData()
//        descriptionData.addCategory("cat_4_job", 1.0)
//        descriptionData.addCategory("cat_5_job", .9)
//        descriptionData.addCategory("cat_6_job", .8)
//        descriptionData.addCategory("cat_1_job", .7)
//        setJobMetaData(job1, titleData, descriptionData)
//
//        val allJobs = ArrayList<Job>()
//        allJobs.add(job1)
//        `when`(jobRespository.findAll()).thenReturn(allJobs)
//
//        val expectedResults = ArrayList<Job>()
//
//        assertEquals(expectedResults, matcher.match(resumeCategoryOne))
//    }
//
//    @Test
//    fun resumeWith1CategoryAndJobCategoryInTitleThirdMatch_shouldMatchNoJob() {
//        val job1 = Job()
//        val titleData = NaiveBayesMetaData()
//        titleData.addCategory("cat_2_job", 1.0)
//        titleData.addCategory("cat_3_job", .9)
//        titleData.addCategory("cat_1_job", .7)
//        val descriptionData = NaiveBayesMetaData()
//        descriptionData.addCategory("cat_4_job", 1.0)
//        descriptionData.addCategory("cat_5_job", .9)
//        descriptionData.addCategory("cat_6_job", .8)
//        setJobMetaData(job1, titleData, descriptionData)
//
//        val allJobs = ArrayList<Job>()
//        allJobs.add(job1)
//        `when`(jobRespository.findAll()).thenReturn(allJobs)
//
//        val expectedResults = ArrayList<Job>()
//
//        assertEquals(expectedResults, matcher.match(resumeCategoryOne))
//    }
//
//    @Test
//    fun resumeWith1CategoryTwiceWith2SuffixAndJobCategoryInTitleThirdMatch_shouldMatchJobWith258StarRating() {
//        val job1 = Job()
//        val titleData = NaiveBayesMetaData()
//        titleData.addCategory("cat_1_job", .7)
//        val descriptionData = NaiveBayesMetaData()
//        descriptionData.addCategory("cat_4_job", 1.0)
//        setJobMetaData(job1, titleData, descriptionData)
//
//        val allJobs = ArrayList<Job>()
//        allJobs.add(job1)
//        `when`(jobRespository.findAll()).thenReturn(allJobs)
//
//        val expectedResults = ArrayList<Job>()
//        expectedResults.add(job1)
//
//
//        val categories = ArrayList<String>()
//        categories.add("cat_1_resume")
//        categories.add("cat_1_job")
//        resumeCategoryOne = createResume(categories)
//
//        assertEquals(expectedResults, matcher.match(resumeCategoryOne))
//        assertEquals(2.58, expectedResults[0].getStarRating(), 0.01)
//    }

}
//
//class MatchAgainstJob : Base() {
//    @Test
//    fun nullJob_shouldReturnEmptyList() {
//        assertEquals(0, matcher.matchInverse(null).size())
//    }
//
//    @Test
//    fun jobWithoutCategories_shouldReturnEmptyList() {
//        val job = Job()
//        assertEquals(0, matcher.matchInverse(job).size())
//    }
//
//    @Test
//    fun jobWithTwoCategory_shouldMatchOneResumeWithStarRating258() {
//
//        val allResumes = ArrayList<Resume>()
//        allResumes.add(resumeCategoryOne)
//        `when`(resumeRespository.findAll()).thenReturn(allResumes)
//
//
//        val expectedResults = ArrayList<Resume>()
//        expectedResults.add(resumeCategoryOne)
//
//        val results = matcher.matchInverse(jobTitleCat1DescCat2)
//
//        assertEquals(expectedResults, results)
//        assertEquals(2.58, results.get(0).getStarRating(), 0.01)
//    }
//
//    @Test
//    fun jobWithTwoCategory_shouldMatchNoResume() {
//        val allResumes = ArrayList<Resume>()
//        allResumes.add(resumeCategoryOneAndTwo)
//        `when`(resumeRespository.findAll()).thenReturn(allResumes)
//
//        val expectedResults = ArrayList<Resume>()
//
//        assertEquals(expectedResults, matcher.matchInverse(jobTitleCat3DescCat4))
//    }
//
//    @Test
//    fun jobWithSixCategoriesAndOnlySixthMatch_shouldMatchNoResume() {
//        val resumeCategories = ArrayList<String>()
//        resumeCategories.add("cat_1_job")
//        resumeCategories.add("cat_2_job")
//        resumeCategories.add("cat_5_job")
//        resumeCategories.add("cat_6_job")
//        resumeCategories.add("cat_7_job")
//        resumeCategories.add("cat_3_job")
//        val resume = createResume(resumeCategories)
//
//        val allResumes = ArrayList<Resume>()
//        allResumes.add(resume)
//        `when`(resumeRespository.findAll()).thenReturn(allResumes)
//
//        val expectedResults = ArrayList<Resume>()
//
//        assertEquals(expectedResults, matcher.matchInverse(jobTitleCat3DescCat4))
//    }
//
//    @Test
//    fun jobWithCategory1In4thPositionInDescriptionAndResumeWithCategory1_shouldMatchNoResume() {
//        val job1 = Job()
//        val titleData = NaiveBayesMetaData()
//        titleData.addCategory("cat_2_job", 1.0)
//        titleData.addCategory("cat_3_job", .9)
//        val descriptionData = NaiveBayesMetaData()
//        descriptionData.addCategory("cat_4_job", 1.0)
//        descriptionData.addCategory("cat_5_job", .9)
//        descriptionData.addCategory("cat_6_job", .8)
//        descriptionData.addCategory("cat_1_job", .7)
//        setJobMetaData(job1, titleData, descriptionData)
//
//        val allResumes = ArrayList<Resume>()
//        allResumes.add(resumeCategoryOne)
//        `when`(resumeRespository.findAll()).thenReturn(allResumes)
//
//        val expectedResults = ArrayList<Resume>()
//
//        assertEquals(expectedResults, matcher.matchInverse(job1))
//    }
//
//    @Test
//    fun jobWithCat1In3rdPositionInTitleAndResumeWithCategory1_shoudMatchNoResume() {
//        val job1 = Job()
//        val titleData = NaiveBayesMetaData()
//        titleData.addCategory("cat_2_job", 1.0)
//        titleData.addCategory("cat_3_job", .9)
//        titleData.addCategory("cat_1_job", .7)
//        val descriptionData = NaiveBayesMetaData()
//        descriptionData.addCategory("cat_4_job", 1.0)
//        descriptionData.addCategory("cat_5_job", .9)
//        descriptionData.addCategory("cat_6_job", .8)
//        setJobMetaData(job1, titleData, descriptionData)
//
//        val allResumes = ArrayList<Resume>()
//        allResumes.add(resumeCategoryOne)
//        `when`(resumeRespository.findAll()).thenReturn(allResumes)
//
//        val expectedResults = ArrayList<Resume>()
//
//        assertEquals(expectedResults, matcher.matchInverse(job1))
//    }
//
//    @Test
//    fun jobWithCat1In3rdPositionWithRepeatedCat2InTitleAndResumeWithCategory1_shoudMatchResume() {
//        val job1 = Job()
//        val titleData = NaiveBayesMetaData()
//        titleData.addCategory("cat_2_job", 1.0)
//        titleData.addCategory("cat_2_resume", .9)
//        titleData.addCategory("cat_1_job", .7)
//        val descriptionData = NaiveBayesMetaData()
//        descriptionData.addCategory("cat_4_job", 1.0)
//        descriptionData.addCategory("cat_5_job", .9)
//        descriptionData.addCategory("cat_6_job", .8)
//        setJobMetaData(job1, titleData, descriptionData)
//
//        val allResumes = ArrayList<Resume>()
//        allResumes.add(resumeCategoryOne)
//        `when`(resumeRespository.findAll()).thenReturn(allResumes)
//
//        val expectedResults = ArrayList<Resume>()
//        expectedResults.add(resumeCategoryOne)
//
//        val results = matcher.matchInverse(job1)
//        assertEquals(expectedResults, results)
//        assertEquals(1.20, results.get(0).getStarRating(), 0.01)
//    }
//
//    @Test
//    fun jobCategory1DuplicatedAndResumeWithCategory1_shoudMatchResume() {
//        val job1 = Job()
//        val titleData = NaiveBayesMetaData()
//        titleData.addCategory("cat_2_job", 1.0)
//        titleData.addCategory("cat_2_resume", .9)
//        titleData.addCategory("cat_1_job", .7)
//        val descriptionData = NaiveBayesMetaData()
//        descriptionData.addCategory("cat_4_job", 1.0)
//        descriptionData.addCategory("cat_1_resume", .7)
//        descriptionData.addCategory("cat_5_job", .9)
//        descriptionData.addCategory("cat_6_job", .8)
//        setJobMetaData(job1, titleData, descriptionData)
//
//        val allResumes = ArrayList<Resume>()
//        allResumes.add(resumeCategoryOne)
//        `when`(resumeRespository.findAll()).thenReturn(allResumes)
//
//        val expectedResults = ArrayList<Resume>()
//        expectedResults.add(resumeCategoryOne)
//
//        val results = matcher.matchInverse(job1)
//        assertEquals(expectedResults, results)
//        assertEquals(1.20, results.get(0).getStarRating(), 0.01)
//    }
//}
//
//class MatchSimilarity : Base() {
//    @Test
//    fun nullResume_shouldReturn0() {
//        assertEquals(0, matcher.matchSimilarity(null, jobTitleCat1DescCat2), 1)
//    }
//
//    @Test
//    fun nullJob_shouldReturn0() {
//        assertEquals(0, matcher.matchSimilarity(resumeCategoryOne, null), 1)
//    }
//
//    @Test
//    fun resumeWithoutCategories_shouldReturn0() {
//        assertEquals(0, matcher.matchSimilarity(Resume(), jobTitleCat1DescCat2), 1)
//    }
//
//    @Test
//    fun jobWithoutCategories_shouldReturn0() {
//        assertEquals(0, matcher.matchSimilarity(resumeCategoryOne, Job()), 1)
//    }
//
//    @Test
//    fun jobWithCat1InTitleAnd2InDescription_ResumeWithCat1And2_shouldReturn381() {
//        assertEquals(3.87, matcher.matchSimilarity(resumeCategoryOneAndTwo, jobTitleCat1DescCat2), 0.01)
//    }
//}
//
//class CheckStarRating : Base() {
//    @Test
//    fun twoListsWith1ElementEqual_shouldReturn5Stars() {
//        val element = ArrayList<String>()
//        element.add("cat_1")
//        element.add("cat_2")
//        element.add("cat_3")
//        element.add("cat_4")
//        element.add("cat_5")
//        assertEquals(5, matcher.calculateStarRating(element, element), 0.01)
//    }
//
//
//    @Test
//    fun twoListsWith1ElementDifferent_shouldReturn0Stars() {
//        val base = ArrayList<String>()
//        base.add("cat_1")
//        val compare = ArrayList<String>()
//        compare.add("cat_2")
//        assertEquals(0, matcher.calculateStarRating(base, compare), 0.01)
//    }
//
//    @Test
//    fun twoListsWith1ElementDifferent_firstBaseCategoryInSecondCompare_shouldReturn0Stars() {
//        val base = ArrayList<String>()
//        base.add("cat_1")
//        val compare = ArrayList<String>()
//        compare.add("cat_2")
//        compare.add("cat_1")
//        assertEquals(1.94, matcher.calculateStarRating(base, compare), 0.01)
//    }
//
//    @Test
//    fun twoListsWith1ElementDifferent_firstAndSecondBaseCategoryInForthAndThirdCompare_shouldReturn0Stars() {
//        val base = ArrayList<String>()
//        base.add("cat_1")
//        base.add("cat_2")
//        val compare = ArrayList<String>()
//        compare.add("cat_3")
//        compare.add("cat_4")
//        compare.add("cat_2")
//        compare.add("cat_1")
//        assertEquals(2.42, matcher.calculateStarRating(base, compare), 0.01)
//    }
//}