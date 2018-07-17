package org.metplus.curriculum.cruncher.expressionCruncher;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.metplus.curriculum.database.domain.*;
import org.metplus.curriculum.database.repository.JobRepository;
import org.metplus.curriculum.database.repository.ResumeRepository;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by joao on 3/21/16.
 */
@RunWith(Suite.class)
@SuiteClasses({MatcherImplTest.MatchResumes.class,
MatcherImplTest.MatchJobs.class,
MatcherImplTest.MatchResumesWithJobObject.class})
public class MatcherImplTest {
    @RunWith(MockitoJUnitRunner.class)
    public static class MatchResumes {
        @Mock
        private ResumeRepository resumeRepository;
        @Mock
        private JobRepository jobRepository;

        private CruncherImpl cruncher;

        private MatcherImpl resumeMatcher;

        // Lorem ipsum dolor sit amet, consectetur adipiscing elit.
        // Aenean quis odio ut neque venenatis iaculis nec eu diam.
        // Sed a libero odio. Suspendisse iaculis velit nec sodales fermentum.
        // Suspendisse potenti. Donec ultricies nulla vitae facilisis tempor.
        // Cras et pretium augue. Maecenas viverra risus nibh, vitae faucibus massa fermentum vitae.
        // Donec eget urna nec nisl pretium maximus.
        private List<Resume> initialize() {
            cruncher = new CruncherImpl();
            List<Resume> resumes = new ArrayList<>();
            Resume resume1 = new Resume("user1");
            ExpressionCruncherMetaData dataResume1 = new ExpressionCruncherMetaData();
            dataResume1.addField("lorem", new MetaDataField(1));
            dataResume1.addField("ipsum", new MetaDataField(1));
            dataResume1.addField("potenti", new MetaDataField(3));
            dataResume1.addField("iaculis", new MetaDataField(2));
            dataResume1.addField("fermentum", new MetaDataField(4));
            dataResume1.setMostReferedExpression("fermentum");
            Map<String, MetaData> metaDataResume1 = new HashMap<>();
            metaDataResume1.put(cruncher.getCruncherName(), dataResume1);
            resume1.setMetaData(metaDataResume1);

            Resume resume2 = new Resume("user2");
            ExpressionCruncherMetaData dataResume2 = new ExpressionCruncherMetaData();
            dataResume2.addField("sodales", new MetaDataField(1));
            dataResume2.addField("facilisis", new MetaDataField(1));
            dataResume2.addField("fermentum", new MetaDataField(2));
            dataResume2.addField("iaculis", new MetaDataField(4));
            dataResume2.addField("ipsum", new MetaDataField(12));
            dataResume2.setMostReferedExpression("ipsum");
            Map<String, MetaData> metaDataResume2 = new HashMap<>();
            metaDataResume2.put(cruncher.getCruncherName(), dataResume2);
            resume2.setMetaData(metaDataResume2);

            Resume resume3 = new Resume("user3");
            ExpressionCruncherMetaData dataResume3 = new ExpressionCruncherMetaData();
            dataResume3.addField("sodales", new MetaDataField(1));
            dataResume3.addField("facilisis", new MetaDataField(1));
            dataResume3.addField("fermentum", new MetaDataField(4));
            dataResume3.addField("iaculis", new MetaDataField(2));
            dataResume3.addField("ipsum", new MetaDataField(10));
            dataResume3.setMostReferedExpression("ipsum");
            Map<String, MetaData> metaDataResume3 = new HashMap<>();
            metaDataResume3.put(cruncher.getCruncherName(), dataResume3);
            resume3.setMetaData(metaDataResume3);

            resumes.add(resume1);
            resumes.add(resume2);
            resumes.add(resume3);
            return resumes;
        }

        @Test
        public void foundNone() {

            Mockito.when(resumeRepository.resumesOnCriteria(Mockito.any())).thenReturn(initialize());
            resumeMatcher = new MatcherImpl(cruncher, resumeRepository, jobRepository);

            assertEquals(0, resumeMatcher.match("pico", "pico de gallo").size());
        }

        @Test
        public void foundTwo() {

            Mockito.when(resumeRepository.resumesOnCriteria(Mockito.any())).thenReturn(initialize());
            resumeMatcher = new MatcherImpl(cruncher, resumeRepository, jobRepository);
            List<Resume> result = resumeMatcher.match("ipsum", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean quis odio ut neque venenatis iaculis nec eu diam. Sed a libero odio. Suspendisse iaculis velit nec sodales fermentum. Suspendisse potenti. Donec ultricies nulla vitae facilisis tempor. Cras et pretium augue. Maecenas viverra risus nibh, vitae faucibus massa fermentum vitae. Donec eget urna nec nisl pretium maximus. ");
            assertEquals(2, result.size());
            assertEquals("user2", result.get(0).getUserId());
            assertEquals("user3", result.get(1).getUserId());
        }

        @Test
        public void foundThree() {

            Mockito.when(resumeRepository.resumesOnCriteria(Mockito.any())).thenReturn(initialize());
            resumeMatcher = new MatcherImpl(cruncher, resumeRepository, jobRepository);
            List<Resume> result = resumeMatcher.match("ipsum", "fermentum ipsum dolor sit amet, fermentum adipiscing elit. fermentum fermentum quis odio ut neque venenatis iaculis nec eu diam. fermentum Sed a libero odio. Suspendisse iaculis velit nec sodales fermentum. Suspendisse potenti. Donec ultricies nulla vitae facilisis tempor. Cras et pretium augue. Maecenas viverra risus nibh, vitae faucibus massa fermentum vitae. Donec eget urna nec nisl pretium maximus. ");
            assertEquals(3, result.size());
            assertEquals("user2", result.get(0).getUserId());
            assertEquals("user3", result.get(1).getUserId());
            assertEquals("user1", result.get(2).getUserId());
        }
    }

    @RunWith(MockitoJUnitRunner.class)
    public static class MatchResumesWithJobObject {
        @Mock
        private ResumeRepository resumeRepository;
        @Mock
        private JobRepository jobRepository;

        private CruncherImpl cruncher;

        private MatcherImpl resumeMatcher;

        // Lorem ipsum dolor sit amet, consectetur adipiscing elit.
        // Aenean quis odio ut neque venenatis iaculis nec eu diam.
        // Sed a libero odio. Suspendisse iaculis velit nec sodales fermentum.
        // Suspendisse potenti. Donec ultricies nulla vitae facilisis tempor.
        // Cras et pretium augue. Maecenas viverra risus nibh, vitae faucibus massa fermentum vitae.
        // Donec eget urna nec nisl pretium maximus.
        private List<Resume> initialize() {
            cruncher = new CruncherImpl();
            List<Resume> resumes = new ArrayList<>();
            Resume resume1 = new Resume("user1");
            ExpressionCruncherMetaData dataResume1 = new ExpressionCruncherMetaData();
            dataResume1.addField("lorem", new MetaDataField(1));
            dataResume1.addField("ipsum", new MetaDataField(1));
            dataResume1.addField("potenti", new MetaDataField(3));
            dataResume1.addField("iaculis", new MetaDataField(2));
            dataResume1.addField("fermentum", new MetaDataField(4));
            dataResume1.setMostReferedExpression("fermentum");
            Map<String, MetaData> metaDataResume1 = new HashMap<>();
            metaDataResume1.put(cruncher.getCruncherName(), dataResume1);
            resume1.setMetaData(metaDataResume1);

            Resume resume2 = new Resume("user2");
            ExpressionCruncherMetaData dataResume2 = new ExpressionCruncherMetaData();
            dataResume2.addField("sodales", new MetaDataField(1));
            dataResume2.addField("facilisis", new MetaDataField(1));
            dataResume2.addField("fermentum", new MetaDataField(2));
            dataResume2.addField("iaculis", new MetaDataField(4));
            dataResume2.addField("ipsum", new MetaDataField(12));
            dataResume2.setMostReferedExpression("ipsum");
            Map<String, MetaData> metaDataResume2 = new HashMap<>();
            metaDataResume2.put(cruncher.getCruncherName(), dataResume2);
            resume2.setMetaData(metaDataResume2);

            Resume resume3 = new Resume("user3");
            ExpressionCruncherMetaData dataResume3 = new ExpressionCruncherMetaData();
            dataResume3.addField("sodales", new MetaDataField(1));
            dataResume3.addField("facilisis", new MetaDataField(1));
            dataResume3.addField("fermentum", new MetaDataField(4));
            dataResume3.addField("iaculis", new MetaDataField(2));
            dataResume3.addField("ipsum", new MetaDataField(10));
            dataResume3.setMostReferedExpression("ipsum");
            Map<String, MetaData> metaDataResume3 = new HashMap<>();
            metaDataResume3.put(cruncher.getCruncherName(), dataResume3);
            resume3.setMetaData(metaDataResume3);

            resumes.add(resume1);
            resumes.add(resume2);
            resumes.add(resume3);
            return resumes;
        }

        private void crunchJob(Job job) {
            Map<String, MetaData> allDescriptionMetaData = new HashMap<>();
            Map<String, MetaData> allTitleMetaData = new HashMap<>();
            MetaData titleMetaData = (MetaData) cruncher.crunch(job.getTitle());
            MetaData descriptionMetaData = (MetaData) cruncher.crunch(job.getDescription());
            allTitleMetaData.put(cruncher.getCruncherName(), titleMetaData);
            allDescriptionMetaData.put(cruncher.getCruncherName(), descriptionMetaData);
            DocumentWithMetaData titleData = new DocumentWithMetaData();
            titleData.setMetaData(allTitleMetaData);
            job.setTitleMetaData(titleData);
            DocumentWithMetaData descriptionData = new DocumentWithMetaData();
            descriptionData.setMetaData(allDescriptionMetaData);
            job.setDescriptionMetaData(descriptionData);
        }

        @Test
        public void foundNone() {

            Mockito.when(resumeRepository.resumesOnCriteria(Mockito.any())).thenReturn(initialize());
            resumeMatcher = new MatcherImpl(cruncher, resumeRepository, jobRepository);

            Job job = new Job();
            job.setTitle("pico");
            job.setDescription("pico de gallo");
            crunchJob(job);
            assertEquals(0, resumeMatcher.matchInverse(job).size());
        }
        @Test
        public void notCrunchedJob() {

            resumeMatcher = new MatcherImpl(cruncher, resumeRepository, jobRepository);

            Job job = new Job();
            job.setTitle("pico");
            job.setDescription("pico de gallo");
            assertNull(resumeMatcher.matchInverse(job));
        }

        @Test
        public void foundTwo() {

            Mockito.when(resumeRepository.resumesOnCriteria(Mockito.any())).thenReturn(initialize());
            resumeMatcher = new MatcherImpl(cruncher, resumeRepository, jobRepository);
            Job job = new Job();
            job.setTitle("ipsum");
            job.setDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean quis odio ut neque venenatis iaculis nec eu diam. Sed a libero odio. Suspendisse iaculis velit nec sodales fermentum. Suspendisse potenti. Donec ultricies nulla vitae facilisis tempor. Cras et pretium augue. Maecenas viverra risus nibh, vitae faucibus massa fermentum vitae. Donec eget urna nec nisl pretium maximus. ");

            crunchJob(job);
            List<Resume> result = resumeMatcher.matchInverse(job);
            assertEquals(2, result.size());
            assertEquals("user2", result.get(0).getUserId());
            assertEquals("user3", result.get(1).getUserId());
        }

        @Test
        public void foundThree() {

            Mockito.when(resumeRepository.resumesOnCriteria(Mockito.any())).thenReturn(initialize());
            resumeMatcher = new MatcherImpl(cruncher, resumeRepository, jobRepository);
            Job job = new Job();
            job.setTitle("ipsum");
            job.setDescription("fermentum ipsum dolor sit amet, fermentum adipiscing elit. fermentum fermentum quis odio ut neque venenatis iaculis nec eu diam. fermentum Sed a libero odio. Suspendisse iaculis velit nec sodales fermentum. Suspendisse potenti. Donec ultricies nulla vitae facilisis tempor. Cras et pretium augue. Maecenas viverra risus nibh, vitae faucibus massa fermentum vitae. Donec eget urna nec nisl pretium maximus. ");

            crunchJob(job);

            List<Resume> result = resumeMatcher.matchInverse(job);
            assertEquals(3, result.size());
            assertEquals("user2", result.get(0).getUserId());
            assertEquals("user3", result.get(1).getUserId());
            assertEquals("user1", result.get(2).getUserId());
        }
    }
    @RunWith(MockitoJUnitRunner.class)
    public static class MatchJobs {
        @Mock
        private ResumeRepository resumeRepository;
        @Mock
        private JobRepository jobRepository;

        private CruncherImpl cruncher;

        private MatcherImpl resumeMatcher;

        private List<Resume> resumes;
        private List<Job> jobs;

        // Lorem ipsum dolor sit amet, consectetur adipiscing elit.
        // Aenean quis odio ut neque venenatis iaculis nec eu diam.
        // Sed a libero odio. Suspendisse iaculis velit nec sodales fermentum.
        // Suspendisse potenti. Donec ultricies nulla vitae facilisis tempor.
        // Cras et pretium augue. Maecenas viverra risus nibh, vitae faucibus massa fermentum vitae.
        // Donec eget urna nec nisl pretium maximus.
        private void initialize() {
            cruncher = new CruncherImpl();
            resumes = new ArrayList<>();
            jobs = new ArrayList<>();
            Resume resume1 = new Resume("user1");
            ExpressionCruncherMetaData dataResume1 = new ExpressionCruncherMetaData();
            dataResume1.addField("lorem", new MetaDataField(1));
            dataResume1.addField("ipsum", new MetaDataField(1));
            dataResume1.addField("potenti", new MetaDataField(3));
            dataResume1.addField("iaculis", new MetaDataField(2));
            dataResume1.addField("fermentum", new MetaDataField(4));
            dataResume1.setMostReferedExpression("fermentum");
            Map<String, MetaData> metaDataResume1 = new HashMap<>();
            metaDataResume1.put(cruncher.getCruncherName(), dataResume1);
            resume1.setMetaData(metaDataResume1);

            Resume resume2 = new Resume("user2");
            ExpressionCruncherMetaData dataResume2 = new ExpressionCruncherMetaData();
            dataResume2.addField("sodales", new MetaDataField(1));
            dataResume2.addField("facilisis", new MetaDataField(1));
            dataResume2.addField("fermentum", new MetaDataField(2));
            dataResume2.addField("iaculis", new MetaDataField(4));
            dataResume2.addField("ipsum", new MetaDataField(12));
            dataResume2.setMostReferedExpression("ipsum");
            Map<String, MetaData> metaDataResume2 = new HashMap<>();
            metaDataResume2.put(cruncher.getCruncherName(), dataResume2);
            resume2.setMetaData(metaDataResume2);

            Resume resume3 = new Resume("user3");
            ExpressionCruncherMetaData dataResume3 = new ExpressionCruncherMetaData();
            dataResume3.addField("sodales", new MetaDataField(1));
            dataResume3.addField("facilisis", new MetaDataField(1));
            dataResume3.addField("fermentum", new MetaDataField(4));
            dataResume3.addField("iaculis", new MetaDataField(2));
            dataResume3.addField("ipsum", new MetaDataField(10));
            dataResume3.setMostReferedExpression("ipsum");
            Map<String, MetaData> metaDataResume3 = new HashMap<>();
            metaDataResume3.put(cruncher.getCruncherName(), dataResume3);
            resume3.setMetaData(metaDataResume3);

            resumes.add(resume1);
            resumes.add(resume2);
            resumes.add(resume3);

            Job job1 = new Job();
            ExpressionCruncherMetaData dataJob1 = new ExpressionCruncherMetaData();
            dataJob1.addField("sodales", new MetaDataField(1));
            dataJob1.addField("facilisis", new MetaDataField(1));
            dataJob1.addField("fermentum", new MetaDataField(4));
            dataJob1.addField("iaculis", new MetaDataField(2));
            dataJob1.addField("ipsum", new MetaDataField(5));
            dataJob1.setMostReferedExpression("ipsum");
            Map<String, MetaData> metaDataJob1 = new HashMap<>();
            metaDataJob1.put(cruncher.getCruncherName(), dataJob1);
            DocumentWithMetaData metaData1 = new DocumentWithMetaData();
            metaData1.setMetaData(metaDataJob1);
            job1.setTitleMetaData(metaData1);
            job1.setJobId("job1");

            Job job2 = new Job();
            ExpressionCruncherMetaData dataJob2 = new ExpressionCruncherMetaData();
            dataJob2.addField("sodales", new MetaDataField(1));
            dataJob2.addField("facilisis", new MetaDataField(1));
            dataJob2.addField("fermentum", new MetaDataField(4));
            dataJob2.addField("iaculis", new MetaDataField(12));
            dataJob2.addField("ipsum", new MetaDataField(10));
            dataJob2.setMostReferedExpression("iaculis");
            Map<String, MetaData> metaDataJob2 = new HashMap<>();
            metaDataJob2.put(cruncher.getCruncherName(), dataJob2);
            DocumentWithMetaData metaData2 = new DocumentWithMetaData();
            metaData2.setMetaData(metaDataJob2);
            job2.setTitleMetaData(metaData2);
            job2.setJobId("job2");

            Job job3 = new Job();
            ExpressionCruncherMetaData dataJob3 = new ExpressionCruncherMetaData();
            dataJob3.addField("sodales", new MetaDataField(1));
            dataJob3.addField("facilisis", new MetaDataField(1));
            dataJob3.addField("fermentum", new MetaDataField(4));
            dataJob3.addField("iaculis", new MetaDataField(2));
            dataJob3.addField("ipsum", new MetaDataField(10));
            dataJob3.setMostReferedExpression("ipsum");
            Map<String, MetaData> metaDataJob3 = new HashMap<>();
            metaDataJob3.put(cruncher.getCruncherName(), dataJob3);
            DocumentWithMetaData metaData3 = new DocumentWithMetaData();
            metaData3.setMetaData(metaDataJob3);
            job3.setTitleMetaData(metaData3);
            job3.setJobId("job3");
            jobs.add(job1);
            jobs.add(job2);
            jobs.add(job3);
        }

        @Test
        public void noJobDatabase() {

            cruncher = new CruncherImpl();
            ExpressionCruncherMetaData metaData = new ExpressionCruncherMetaData();
            metaData.setMostReferedExpression("ipsum");
            metaData.setFields(new HashMap<>());
            Mockito.when(jobRepository.findAll()).thenReturn(new ArrayList<>());
            resumeMatcher = new MatcherImpl(cruncher, resumeRepository, jobRepository);

            Resume resume = new Resume();
            Map<String, MetaData> metaDataResume = new HashMap<>();
            metaDataResume.put(cruncher.getCruncherName(), metaData);
            resume.setMetaData(metaDataResume);
            assertEquals(0, resumeMatcher.match(resume).size());
        }
        @Test
        public void foundNone() {
            initialize();
            ExpressionCruncherMetaData metaData = new ExpressionCruncherMetaData();
            metaData.setMostReferedExpression("bamm");
            metaData.setFields(new HashMap<>());
            Mockito.when(jobRepository.findAll()).thenReturn(jobs);
            resumeMatcher = new MatcherImpl(cruncher, resumeRepository, jobRepository);

            Resume resume = new Resume();
            Map<String, MetaData> metaDataResume = new HashMap<>();
            metaDataResume.put(cruncher.getCruncherName(), metaData);
            resume.setMetaData(metaDataResume);
            assertEquals(0, resumeMatcher.match(resume).size());
        }

        @Test
        public void foundTwo() {
            initialize();
            ExpressionCruncherMetaData metaData = new ExpressionCruncherMetaData();
            metaData.setMostReferedExpression("ipsum");
            metaData.setFields(new HashMap<>());
            resumeMatcher = new MatcherImpl(cruncher, resumeRepository, jobRepository);
            Mockito.when(jobRepository.findAll()).thenReturn(jobs);

            Resume resume = new Resume();
            Map<String, MetaData> metaDataResume = new HashMap<>();
            metaDataResume.put(cruncher.getCruncherName(), metaData);
            resume.setMetaData(metaDataResume);

            List<Job> result = resumeMatcher.match(resume);
            assertEquals(2, result.size());
            assertEquals("job3", result.get(0).getJobId());
            assertEquals("job1", result.get(1).getJobId());
        }

    }
}
