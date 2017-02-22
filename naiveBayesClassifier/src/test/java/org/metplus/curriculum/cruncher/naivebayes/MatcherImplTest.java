package org.metplus.curriculum.cruncher.naivebayes;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.metplus.curriculum.cruncher.CruncherMetaData;
import org.metplus.curriculum.database.domain.*;
import org.metplus.curriculum.database.repository.JobRepository;
import org.metplus.curriculum.database.repository.ResumeRepository;
import org.metplus.curriculum.test.BeforeAfterInterface;
import org.metplus.curriculum.test.BeforeAfterRule;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.mongodb.core.query.Meta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(Suite.class)
@Suite.SuiteClasses({MatcherImplTest.MatchProbability_TwoLists.class,
                     MatcherImplTest.MatchProbability_ListAndMetaData.class,
                     MatcherImplTest.CompareJobResume.class,
                     MatcherImplTest.MatchJobs.class})
public class MatcherImplTest {
    public static class Base implements BeforeAfterInterface {
        @Rule
        public BeforeAfterRule beforeAfter = new BeforeAfterRule(this);
        MatcherImpl matcher;
        @Mock
        ResumeRepository resumeRepository;
        @Mock
        JobRepository jobRepository;
        @Mock
        CruncherImpl cruncher;

        @Override
        public void after() {

        }

        @Override
        public void before() {
            Mockito.when(cruncher.getCruncherName()).thenReturn(CruncherImpl.CRUNCHER_NAME);
            matcher = new MatcherImpl(cruncher,resumeRepository, jobRepository);
        }
    }

    @RunWith(MockitoJUnitRunner.class)
    public static class MatchProbability_TwoLists extends Base {
        List<String> categories1;
        @Override
        public void before() {
            super.before();
            categories1 = new ArrayList<>();
            categories1.add("cat 1_resume");
            categories1.add("cat 2_resume");
            categories1.add("cat 3_job");
            categories1.add("cat 4_job");
            categories1.add("cat 5_job");
        }

        @Test
        public void allMatch() {
            assertEquals(1, matcher.matchProbability(categories1, categories1), 0.01);
        }

        @Test
        public void noMatch() {
            List<String> categories2 = new ArrayList<>();
            categories2.add("a_job");
            categories2.add("b_job");
            categories2.add("v_job");
            categories2.add("d_job");
            categories2.add("e_job");
            assertEquals(0, matcher.matchProbability(categories1, categories2), 0.01);
        }

        @Test
        public void mostRelevantMatchOnly() {
            List<String> categories2 = new ArrayList<>();
            categories2.add("cat 1_resume");
            categories2.add("b_job");
            categories2.add("v_job");
            categories2.add("d_job");
            categories2.add("e_job");
            assertEquals(0.51, matcher.matchProbability(categories1, categories2), 0.01);
        }

        @Test
        public void mostRelevantMatchOnlyMatchOnLastPlace() {
            List<String> categories2 = new ArrayList<>();
            categories2.add("b_job");
            categories2.add("v_job");
            categories2.add("d_job");
            categories2.add("e_job");
            categories2.add("cat 1_resume");
            assertEquals(0.24, matcher.matchProbability(categories1, categories2), 0.01);
        }
        @Test
        public void inverse() {
            List<String> categories2 = new ArrayList<>();
            categories2.add("cat 5_job");
            categories2.add("b_job");
            categories2.add("v_job");
            categories2.add("d_job");
            categories2.add("e_job");
            assertEquals(0.27, matcher.matchProbability(categories1, categories2), 0.01);
        }
    }
    @RunWith(MockitoJUnitRunner.class)
    public static class MatchProbability_ListAndMetaData extends Base {
        List<String> categories1;
        @Override
        public void before() {
            super.before();
            categories1 = new ArrayList<>();
            categories1.add("cat 1_job");
            categories1.add("cat 2_job");
            categories1.add("cat 3_job");
            categories1.add("cat 4_resume");
            categories1.add("cat 5_resume");
        }

        @Test
        public void allMatch() {
            NaiveBayesMetaData data = new NaiveBayesMetaData();
            data.addField("cat 1_job", new MetaDataField(5.));
            data.addField("cat 2_job", new MetaDataField(4.));
            data.addField("cat 3_job", new MetaDataField(3.));
            data.addField("cat 4_resume", new MetaDataField(2.));
            data.addField("cat 5_resume", new MetaDataField(1.));
            assertEquals(1, matcher.matchProbability(categories1, data), 0.01);
        }

        @Test
        public void noMatch() {
            NaiveBayesMetaData data = new NaiveBayesMetaData();
            data.addField("other cat_resume", new MetaDataField(3.));
            assertEquals(-1, matcher.matchProbability(categories1, data), 0);
        }

        @Test
        public void mostRelevantMatchOnly() {
            NaiveBayesMetaData data = new NaiveBayesMetaData();
            data.addField("cat 1_job", new MetaDataField(3.));
            assertEquals(0.51, matcher.matchProbability(categories1, data), 0.01);
        }

        @Test
        public void mostRelevantMatchOnlyMatchOnLastPlace() {
            NaiveBayesMetaData data = new NaiveBayesMetaData();
            data.addField("other cat 1_job", new MetaDataField(5.));
            data.addField("other cat 2_job", new MetaDataField(4.));
            data.addField("other cat 3_job", new MetaDataField(3.));
            data.addField("other cat 4_job", new MetaDataField(2.));
            data.addField("cat 1_job", new MetaDataField(1.));
            assertEquals(0.24, matcher.matchProbability(categories1, data), 0.01);
        }
        @Test
        public void inverse() {
            NaiveBayesMetaData data = new NaiveBayesMetaData();
            data.addField("cat 5_resume", new MetaDataField(6.));
            data.addField("other cat 1_job", new MetaDataField(5.));
            data.addField("other cat 2_job", new MetaDataField(4.));
            data.addField("other cat 3_job", new MetaDataField(3.));
            data.addField("other cat 4_job", new MetaDataField(2.));
            assertEquals(0.27, matcher.matchProbability(categories1, data), 0.01);
        }
    }


    @RunWith(MockitoJUnitRunner.class)
    public static class CompareJobResume extends Base {
        protected Resume resume;
        protected Job job;
        @Override
        public void before() {
            super.before();
            resume = new Resume();
            NaiveBayesMetaData data = new NaiveBayesMetaData();
            Map<String, MetaData> metaData = new HashMap<>();
            data.addCategory("cat 1_job", 5.);
            data.addCategory("cat 2_job", 4.);
            data.addCategory("cat 3_job", 3.);
            data.addCategory("cat 4_resume", 2.);
            data.addCategory("cat 5_resume", 1.);
            metaData.put(CruncherImpl.CRUNCHER_NAME, data);
            resume.setMetaData(metaData);

            job = new Job();
        }

        private void setJobMetaData(NaiveBayesMetaData titleMetaData, NaiveBayesMetaData descriptionMetaData) {
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

        @Test
        public void allMatch() {

            NaiveBayesMetaData titleData = new NaiveBayesMetaData();
            Map<String, MetaData> titleMetaData = new HashMap<>();
            titleData.addCategory("cat 1_job", 6.);
            titleData.addCategory("cat 2_job", 5.);
            titleMetaData.put(CruncherImpl.CRUNCHER_NAME, titleData);

            NaiveBayesMetaData descriptionData = new NaiveBayesMetaData();
            Map<String, MetaData> descriptionMetaData = new HashMap<>();
            descriptionData.addCategory("cat 1_job", 6.);
            descriptionData.addCategory("cat 3_job", 5.);
            descriptionData.addCategory("cat 4_resume", 4.);
            descriptionData.addCategory("cat 5_resume", 3.);
            descriptionMetaData.put(CruncherImpl.CRUNCHER_NAME, descriptionData);
            setJobMetaData(titleData, descriptionData);

            assertEquals(5, matcher.matchSimilarity(resume, job), 0.01);
        }

        @Test
        public void noMatch() {
            NaiveBayesMetaData titleData = new NaiveBayesMetaData();
            Map<String, MetaData> titleMetaData = new HashMap<>();
            titleData.addCategory("cat 10_resume", 6.);
            titleMetaData.put(CruncherImpl.CRUNCHER_NAME, titleData);

            NaiveBayesMetaData descriptionData = new NaiveBayesMetaData();
            Map<String, MetaData> descriptionMetaData = new HashMap<>();
            descriptionData.addCategory("cat 11_resume", 6.);
            descriptionData.addCategory("cat 31_resume", 5.);
            descriptionMetaData.put(CruncherImpl.CRUNCHER_NAME, descriptionData);
            setJobMetaData(titleData, descriptionData);
            assertEquals(0, matcher.matchSimilarity(resume, job), 0);
        }

        @Test
        public void mostRelevantMatchOnly() {
            NaiveBayesMetaData titleData = new NaiveBayesMetaData();
            Map<String, MetaData> titleMetaData = new HashMap<>();
            titleData.addCategory("cat 1_job", 6.);
            titleMetaData.put(CruncherImpl.CRUNCHER_NAME, titleData);

            NaiveBayesMetaData descriptionData = new NaiveBayesMetaData();
            Map<String, MetaData> descriptionMetaData = new HashMap<>();
            descriptionData.addCategory("cat 11_resume", 6.);
            descriptionData.addCategory("cat 31_resume", 5.);
            descriptionMetaData.put(CruncherImpl.CRUNCHER_NAME, descriptionData);
            setJobMetaData(titleData, descriptionData);
            assertEquals(2.58, matcher.matchSimilarity(resume, job), 0.01);
        }

        @Test
        public void mostRelevantMatchOnlyMatchOnLastPlace() {
            NaiveBayesMetaData titleData = new NaiveBayesMetaData();
            Map<String, MetaData> titleMetaData = new HashMap<>();
            titleData.addCategory("cat 12_resume", 6.);
            titleMetaData.put(CruncherImpl.CRUNCHER_NAME, titleData);

            NaiveBayesMetaData descriptionData = new NaiveBayesMetaData();
            Map<String, MetaData> descriptionMetaData = new HashMap<>();
            descriptionData.addCategory("cat 11_resume", 6.);
            descriptionData.addCategory("cat 31_resume", 5.);
            descriptionData.addCategory("cat 32_resume", 4.);
            descriptionData.addCategory("cat 1_job", 3.);
            descriptionMetaData.put(CruncherImpl.CRUNCHER_NAME, descriptionData);
            setJobMetaData(titleData, descriptionData);

            assertEquals(1.37, matcher.matchSimilarity(resume, job), 0.01);
        }
        @Test
        public void inverse() {

            NaiveBayesMetaData titleData = new NaiveBayesMetaData();
            Map<String, MetaData> titleMetaData = new HashMap<>();
            titleData.addCategory("cat 12_resume", 6.);
            titleMetaData.put(CruncherImpl.CRUNCHER_NAME, titleData);

            NaiveBayesMetaData descriptionData = new NaiveBayesMetaData();
            Map<String, MetaData> descriptionMetaData = new HashMap<>();
            descriptionData.addCategory("cat 5_resume", 6.);
            descriptionData.addCategory("cat 31_resume", 5.);
            descriptionData.addCategory("cat 32_resume", 4.);
            descriptionData.addCategory("cat 11_resume", 3.);
            descriptionMetaData.put(CruncherImpl.CRUNCHER_NAME, descriptionData);
            setJobMetaData(titleData, descriptionData);
            assertEquals(0.56, matcher.matchSimilarity(resume, job), 0.01);
        }
    }
    @RunWith(MockitoJUnitRunner.class)
    public static class MatchJobs extends Base {
        List<String> categories1;
        NaiveBayesMetaData resumeData;
        List<Job> allJobs;
        Job job1;
        Job job2;
        Job job3;
        Job job4;

        private void setJobMetaData(Job job, NaiveBayesMetaData titleMetaData, NaiveBayesMetaData descriptionMetaData) {
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

        @Override
        public void before() {
            super.before();
            resumeData = new NaiveBayesMetaData();
            resumeData.addField("cat 1_job", new MetaDataField(5.));
            resumeData.addField("cat 2_job", new MetaDataField(4.));
            resumeData.addField("cat 3_job", new MetaDataField(3.));
            resumeData.addField("cat 4_resume", new MetaDataField(2.));
            resumeData.addField("cat 5_resume", new MetaDataField(1.));
            resumeData.addField("cat 6_resume", new MetaDataField(.1));
            job1 = new Job();
            job1.setJobId("1");
            job1.setTitle("Job 1");
            job1.setDescription("desc 1");
            NaiveBayesMetaData titleData = new NaiveBayesMetaData();
            titleData.addCategory("cat 12_resume", 6.);
            setJobMetaData(job1, titleData, new NaiveBayesMetaData());
            job2 = new Job();
            job2.setJobId("2");
            job2.setTitle("Job 2");
            job2.setDescription("desc 2");

            titleData = new NaiveBayesMetaData();
            titleData.addCategory("cat 1_job", 6.);

            NaiveBayesMetaData descriptionData = new NaiveBayesMetaData();
            descriptionData.addCategory("cat 12_resume", 6.);
            setJobMetaData(job2, titleData, descriptionData);

            job3 = new Job();
            job3.setJobId("3");
            job3.setTitle("Job 3");
            job3.setDescription("desc 3");

            titleData = new NaiveBayesMetaData();
            titleData.addCategory("cat 2_job", 6.);
            titleData.addCategory("cat 3_job", 5.);
            titleData.addCategory("cat 11_resume", 4.);

            descriptionData = new NaiveBayesMetaData();
            descriptionData.addCategory("cat 1_job", 6.);
            setJobMetaData(job3, titleData, descriptionData);

            job4 = new Job();
            job4.setJobId("4");
            job4.setTitle("Job 4");
            job4.setDescription("desc 4");

            allJobs = new ArrayList<>();
            List<Job> repJobs = new ArrayList<>();
            repJobs.add(job1);
            repJobs.add(job2);
            repJobs.add(job3);
            repJobs.add(job4);
            Mockito.when(jobRepository.findAll()).thenReturn(repJobs);
        }

        @Test
        public void nullMetadata() {
            List<Job> result = matcher.match((CruncherMetaData) null);
            assertNull(result);
        }

        @Test
        public void allMatch() {
            allJobs.add(job2);
            allJobs.add(job3);

            List<Job> result = matcher.match(resumeData);
            assertEquals(allJobs, result);
            assertEquals(2.58, result.get(0).getStarRating(), 0.01);
            assertEquals(3.87, result.get(1).getStarRating(), 0.01);
        }

        @Test
        public void noMatchFound() {
            resumeData = new NaiveBayesMetaData();
            resumeData.addField("cat 1111_job", new MetaDataField(5.));

            List<Job> result = matcher.match(resumeData);
            assertEquals(0, result.size());
        }
    }
}
