package org.metplus.curriculum.cruncher.naivebayes;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.metplus.curriculum.database.domain.DocumentWithMetaData;
import org.metplus.curriculum.database.domain.Job;
import org.metplus.curriculum.database.domain.MetaData;
import org.metplus.curriculum.database.domain.Resume;
import org.metplus.curriculum.database.repository.JobRepository;
import org.metplus.curriculum.database.repository.ResumeRepository;
import org.metplus.curriculum.test.BeforeAfterInterface;
import org.metplus.curriculum.test.BeforeAfterRule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@RunWith(Suite.class)
@Suite.SuiteClasses({MatchImplTest.MatchAgainstResume.class,
        MatchImplTest.MatchAgainstJob.class,
        MatchImplTest.CheckStarRating.class,
        MatchImplTest.MatchSimilarity.class})
public class MatchImplTest {
    public static class Base implements BeforeAfterInterface {

        protected JobRepository jobRespository = mock(JobRepository.class);
        protected ResumeRepository resumeRespository = mock(ResumeRepository.class);

        @Rule
        public BeforeAfterRule rule = new BeforeAfterRule(this);
        protected MatchImpl matcher;
        protected CruncherImpl cruncher;
        protected Resume resumeCategoryOneAndTwo;
        protected Resume resumeCategoryOne;
        protected Job jobTitleCat1DescCat2;
        protected Job jobTitleCat3DescCat4;

        @Override
        public void before() {
            cruncher = new CruncherImpl();
            matcher = new MatchImpl(cruncher, jobRespository, resumeRespository);


            List<String> categories = new ArrayList<>();
            categories.add("cat_1_job");
            resumeCategoryOne = createResume(categories);

            categories.add("cat_2_job");
            resumeCategoryOneAndTwo = createResume(categories);

            jobTitleCat1DescCat2 = new Job();
            NaiveBayesMetaData titleData = new NaiveBayesMetaData();
            titleData.addCategory("cat_1_job", 1);
            NaiveBayesMetaData descriptionData = new NaiveBayesMetaData();
            descriptionData.addCategory("cat_2_job", 1);
            setJobMetaData(jobTitleCat1DescCat2, titleData, descriptionData);

            jobTitleCat3DescCat4 = new Job();
            titleData = new NaiveBayesMetaData();
            titleData.addCategory("cat_3_job", 1);
            descriptionData = new NaiveBayesMetaData();
            descriptionData.addCategory("cat_4_job", 1);
            setJobMetaData(jobTitleCat3DescCat4, titleData, descriptionData);
        }

        @Override
        public void after() {
            reset(jobRespository, resumeRespository);
        }

        protected void setJobMetaData(Job job,
                                      NaiveBayesMetaData titleMetaData,
                                      NaiveBayesMetaData descriptionMetaData) {
            Map<String, MetaData> allDescriptionMetaData = new HashMap<>();
            Map<String, MetaData> allTitleMetaData = new HashMap<>();
            allTitleMetaData.put(CruncherImpl.CRUNCHER_NAME, titleMetaData);
            allDescriptionMetaData.put(CruncherImpl.CRUNCHER_NAME, descriptionMetaData);
            DocumentWithMetaData titleData = new DocumentWithMetaData();
            titleData.setMetaData(allTitleMetaData);
            job.setTitleMetaData(titleData);
            DocumentWithMetaData descriptionData = new DocumentWithMetaData();
            descriptionData.setMetaData(allDescriptionMetaData);
            job.setDescriptionMetaData(descriptionData);
        }

        protected Resume createResume(List<String> categories) {
            NaiveBayesMetaData resumeMetaData = new NaiveBayesMetaData();
            double probability = 1;
            for (String category : categories) {
                resumeMetaData.addCategory(category, probability);
                probability -= .1;
            }
            Map<String, MetaData> resumeCruncherMetaData = new HashMap<>();
            resumeCruncherMetaData.put(cruncher.getCruncherName(), resumeMetaData);
            Resume resume = new Resume();
            resume.setMetaData(resumeCruncherMetaData);
            return resume;
        }
    }

    public static class MatchAgainstResume extends Base {
        @Test
        public void nullResume_shouldReturnEmptyList() {
            assertEquals(0, matcher.match(null).size());
        }

        @Test
        public void resumeWithoutCategories_shouldReturnEmptyList() {
            Resume resume = new Resume();
            assertEquals(0, matcher.match(resume).size());
        }

        @Test
        public void resumeWithTwoCategory_shouldMatchOneJobWith387StarRating() {
            List<Job> allJobs = new ArrayList<>();
            allJobs.add(jobTitleCat1DescCat2);
            when(jobRespository.findAll()).thenReturn(allJobs);


            List<Job> expectedResults = new ArrayList<>();
            expectedResults.add(jobTitleCat1DescCat2);

            List<Job> results = matcher.match(resumeCategoryOneAndTwo);

            assertEquals(expectedResults, results);
            assertEquals(3.87, results.get(0).getStarRating(), 0.01);
        }

        @Test
        public void resumeWithTwoCategory_shouldMatchNoJob() {

            List<Job> allJobs = new ArrayList<>();
            allJobs.add(jobTitleCat3DescCat4);
            when(jobRespository.findAll()).thenReturn(allJobs);

            List<Job> expectedResults = new ArrayList<>();

            assertEquals(expectedResults, matcher.match(resumeCategoryOneAndTwo));
        }

        @Test
        public void resumeWithSixCategoryAndOnlySixMatch_shouldMatchNoJob() {
            List<String> resumeCategories = new ArrayList<>();
            resumeCategories.add("cat_1_job");
            resumeCategories.add("cat_2_job");
            resumeCategories.add("cat_5_job");
            resumeCategories.add("cat_6_job");
            resumeCategories.add("cat_7_job");
            resumeCategories.add("cat_3_job");
            Resume resume = createResume(resumeCategories);

            List<Job> allJobs = new ArrayList<>();
            allJobs.add(jobTitleCat3DescCat4);
            when(jobRespository.findAll()).thenReturn(allJobs);

            List<Job> expectedResults = new ArrayList<>();

            assertEquals(expectedResults, matcher.match(resume));
        }

        @Test
        public void resumeWith1CategoryAndJobCategoryInDescriptionSixMatch_shouldMatchNoJob() {
            Job job1 = new Job();
            NaiveBayesMetaData titleData = new NaiveBayesMetaData();
            titleData.addCategory("cat_2_job", 1);
            titleData.addCategory("cat_3_job", .9);
            NaiveBayesMetaData descriptionData = new NaiveBayesMetaData();
            descriptionData.addCategory("cat_4_job", 1);
            descriptionData.addCategory("cat_5_job", .9);
            descriptionData.addCategory("cat_6_job", .8);
            descriptionData.addCategory("cat_1_job", .7);
            setJobMetaData(job1, titleData, descriptionData);

            List<Job> allJobs = new ArrayList<>();
            allJobs.add(job1);
            when(jobRespository.findAll()).thenReturn(allJobs);

            List<Job> expectedResults = new ArrayList<>();

            assertEquals(expectedResults, matcher.match(resumeCategoryOne));
        }

        @Test
        public void resumeWith1CategoryAndJobCategoryInTitleThirdMatch_shouldMatchNoJob() {
            Job job1 = new Job();
            NaiveBayesMetaData titleData = new NaiveBayesMetaData();
            titleData.addCategory("cat_2_job", 1);
            titleData.addCategory("cat_3_job", .9);
            titleData.addCategory("cat_1_job", .7);
            NaiveBayesMetaData descriptionData = new NaiveBayesMetaData();
            descriptionData.addCategory("cat_4_job", 1);
            descriptionData.addCategory("cat_5_job", .9);
            descriptionData.addCategory("cat_6_job", .8);
            setJobMetaData(job1, titleData, descriptionData);

            List<Job> allJobs = new ArrayList<>();
            allJobs.add(job1);
            when(jobRespository.findAll()).thenReturn(allJobs);

            List<Job> expectedResults = new ArrayList<>();

            assertEquals(expectedResults, matcher.match(resumeCategoryOne));
        }

        @Test
        public void resumeWith1CategoryTwiceWith2SuffixAndJobCategoryInTitleThirdMatch_shouldMatchJobWith258StarRating() {
            Job job1 = new Job();
            NaiveBayesMetaData titleData = new NaiveBayesMetaData();
            titleData.addCategory("cat_1_job", .7);
            NaiveBayesMetaData descriptionData = new NaiveBayesMetaData();
            descriptionData.addCategory("cat_4_job", 1);
            setJobMetaData(job1, titleData, descriptionData);

            List<Job> allJobs = new ArrayList<>();
            allJobs.add(job1);
            when(jobRespository.findAll()).thenReturn(allJobs);

            List<Job> expectedResults = new ArrayList<>();
            expectedResults.add(job1);


            List<String> categories = new ArrayList<>();
            categories.add("cat_1_resume");
            categories.add("cat_1_job");
            resumeCategoryOne = createResume(categories);

            assertEquals(expectedResults, matcher.match(resumeCategoryOne));
            assertEquals(2.58, expectedResults.get(0).getStarRating(), 0.01);
        }

    }

    public static class MatchAgainstJob extends Base {
        @Test
        public void nullJob_shouldReturnEmptyList() {
            assertEquals(0, matcher.matchInverse(null).size());
        }

        @Test
        public void jobWithoutCategories_shouldReturnEmptyList() {
            Job job = new Job();
            assertEquals(0, matcher.matchInverse(job).size());
        }

        @Test
        public void jobWithTwoCategory_shouldMatchOneResumeWithStarRating258() {

            List<Resume> allResumes = new ArrayList<>();
            allResumes.add(resumeCategoryOne);
            when(resumeRespository.findAll()).thenReturn(allResumes);


            List<Resume> expectedResults = new ArrayList<>();
            expectedResults.add(resumeCategoryOne);

            List<Resume> results = matcher.matchInverse(jobTitleCat1DescCat2);

            assertEquals(expectedResults, results);
            assertEquals(2.58, results.get(0).getStarRating(), 0.01);
        }

        @Test
        public void jobWithTwoCategory_shouldMatchNoResume() {
            List<Resume> allResumes = new ArrayList<>();
            allResumes.add(resumeCategoryOneAndTwo);
            when(resumeRespository.findAll()).thenReturn(allResumes);

            List<Resume> expectedResults = new ArrayList<>();

            assertEquals(expectedResults, matcher.matchInverse(jobTitleCat3DescCat4));
        }

        @Test
        public void jobWithSixCategoriesAndOnlySixthMatch_shouldMatchNoResume() {
            List<String> resumeCategories = new ArrayList<>();
            resumeCategories.add("cat_1_job");
            resumeCategories.add("cat_2_job");
            resumeCategories.add("cat_5_job");
            resumeCategories.add("cat_6_job");
            resumeCategories.add("cat_7_job");
            resumeCategories.add("cat_3_job");
            Resume resume = createResume(resumeCategories);

            List<Resume> allResumes = new ArrayList<>();
            allResumes.add(resume);
            when(resumeRespository.findAll()).thenReturn(allResumes);

            List<Resume> expectedResults = new ArrayList<>();

            assertEquals(expectedResults, matcher.matchInverse(jobTitleCat3DescCat4));
        }

        @Test
        public void jobWithCategory1In4thPositionInDescriptionAndResumeWithCategory1_shouldMatchNoResume() {
            Job job1 = new Job();
            NaiveBayesMetaData titleData = new NaiveBayesMetaData();
            titleData.addCategory("cat_2_job", 1);
            titleData.addCategory("cat_3_job", .9);
            NaiveBayesMetaData descriptionData = new NaiveBayesMetaData();
            descriptionData.addCategory("cat_4_job", 1);
            descriptionData.addCategory("cat_5_job", .9);
            descriptionData.addCategory("cat_6_job", .8);
            descriptionData.addCategory("cat_1_job", .7);
            setJobMetaData(job1, titleData, descriptionData);

            List<Resume> allResumes = new ArrayList<>();
            allResumes.add(resumeCategoryOne);
            when(resumeRespository.findAll()).thenReturn(allResumes);

            List<Resume> expectedResults = new ArrayList<>();

            assertEquals(expectedResults, matcher.matchInverse(job1));
        }

        @Test
        public void jobWithCat1In3rdPositionInTitleAndResumeWithCategory1_shoudMatchNoResume() {
            Job job1 = new Job();
            NaiveBayesMetaData titleData = new NaiveBayesMetaData();
            titleData.addCategory("cat_2_job", 1);
            titleData.addCategory("cat_3_job", .9);
            titleData.addCategory("cat_1_job", .7);
            NaiveBayesMetaData descriptionData = new NaiveBayesMetaData();
            descriptionData.addCategory("cat_4_job", 1);
            descriptionData.addCategory("cat_5_job", .9);
            descriptionData.addCategory("cat_6_job", .8);
            setJobMetaData(job1, titleData, descriptionData);

            List<Resume> allResumes = new ArrayList<>();
            allResumes.add(resumeCategoryOne);
            when(resumeRespository.findAll()).thenReturn(allResumes);

            List<Resume> expectedResults = new ArrayList<>();

            assertEquals(expectedResults, matcher.matchInverse(job1));
        }

        @Test
        public void jobWithCat1In3rdPositionWithRepeatedCat2InTitleAndResumeWithCategory1_shoudMatchResume() {
            Job job1 = new Job();
            NaiveBayesMetaData titleData = new NaiveBayesMetaData();
            titleData.addCategory("cat_2_job", 1);
            titleData.addCategory("cat_2_resume", .9);
            titleData.addCategory("cat_1_job", .7);
            NaiveBayesMetaData descriptionData = new NaiveBayesMetaData();
            descriptionData.addCategory("cat_4_job", 1);
            descriptionData.addCategory("cat_5_job", .9);
            descriptionData.addCategory("cat_6_job", .8);
            setJobMetaData(job1, titleData, descriptionData);

            List<Resume> allResumes = new ArrayList<>();
            allResumes.add(resumeCategoryOne);
            when(resumeRespository.findAll()).thenReturn(allResumes);

            List<Resume> expectedResults = new ArrayList<>();
            expectedResults.add(resumeCategoryOne);

            List<Resume> results = matcher.matchInverse(job1);
            assertEquals(expectedResults, results);
            assertEquals(1.20, results.get(0).getStarRating(), 0.01);
        }

        @Test
        public void jobCategory1DuplicatedAndResumeWithCategory1_shoudMatchResume() {
            Job job1 = new Job();
            NaiveBayesMetaData titleData = new NaiveBayesMetaData();
            titleData.addCategory("cat_2_job", 1);
            titleData.addCategory("cat_2_resume", .9);
            titleData.addCategory("cat_1_job", .7);
            NaiveBayesMetaData descriptionData = new NaiveBayesMetaData();
            descriptionData.addCategory("cat_4_job", 1);
            descriptionData.addCategory("cat_1_resume", .7);
            descriptionData.addCategory("cat_5_job", .9);
            descriptionData.addCategory("cat_6_job", .8);
            setJobMetaData(job1, titleData, descriptionData);

            List<Resume> allResumes = new ArrayList<>();
            allResumes.add(resumeCategoryOne);
            when(resumeRespository.findAll()).thenReturn(allResumes);

            List<Resume> expectedResults = new ArrayList<>();
            expectedResults.add(resumeCategoryOne);

            List<Resume> results = matcher.matchInverse(job1);
            assertEquals(expectedResults, results);
            assertEquals(1.20, results.get(0).getStarRating(), 0.01);
        }
    }

    public static class MatchSimilarity extends Base {
        @Test
        public void nullResume_shouldReturn0() {
            assertEquals(0, matcher.matchSimilarity(null, jobTitleCat1DescCat2), 1);
        }

        @Test
        public void nullJob_shouldReturn0() {
            assertEquals(0, matcher.matchSimilarity(resumeCategoryOne, null), 1);
        }

        @Test
        public void resumeWithoutCategories_shouldReturn0() {
            assertEquals(0, matcher.matchSimilarity(new Resume(), jobTitleCat1DescCat2), 1);
        }

        @Test
        public void jobWithoutCategories_shouldReturn0() {
            assertEquals(0, matcher.matchSimilarity(resumeCategoryOne, new Job()), 1);
        }

        @Test
        public void jobWithCat1InTitleAnd2InDescription_ResumeWithCat1And2_shouldReturn381() {
            assertEquals(3.87, matcher.matchSimilarity(resumeCategoryOneAndTwo, jobTitleCat1DescCat2), 0.01);
        }
    }

    public static class CheckStarRating extends Base {
        @Test
        public void twoListsWith1ElementEqual_shouldReturn5Stars() {
            List<String> element = new ArrayList<>();
            element.add("cat_1");
            element.add("cat_2");
            element.add("cat_3");
            element.add("cat_4");
            element.add("cat_5");
            assertEquals(5, matcher.calculateStarRating(element, element), 0.01);
        }


        @Test
        public void twoListsWith1ElementDifferent_shouldReturn0Stars() {
            List<String> base = new ArrayList<>();
            base.add("cat_1");
            List<String> compare = new ArrayList<>();
            compare.add("cat_2");
            assertEquals(0, matcher.calculateStarRating(base, compare), 0.01);
        }

        @Test
        public void twoListsWith1ElementDifferent_firstBaseCategoryInSecondCompare_shouldReturn0Stars() {
            List<String> base = new ArrayList<>();
            base.add("cat_1");
            List<String> compare = new ArrayList<>();
            compare.add("cat_2");
            compare.add("cat_1");
            assertEquals(1.94, matcher.calculateStarRating(base, compare), 0.01);
        }

        @Test
        public void twoListsWith1ElementDifferent_firstAndSecondBaseCategoryInForthAndThirdCompare_shouldReturn0Stars() {
            List<String> base = new ArrayList<>();
            base.add("cat_1");
            base.add("cat_2");
            List<String> compare = new ArrayList<>();
            compare.add("cat_3");
            compare.add("cat_4");
            compare.add("cat_2");
            compare.add("cat_1");
            assertEquals(2.42, matcher.calculateStarRating(base, compare), 0.01);
        }
    }
}
