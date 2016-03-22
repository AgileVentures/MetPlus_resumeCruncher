package org.metplus.curriculum.cruncher.expressionCruncher;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.metplus.curriculum.cruncher.Cruncher;
import org.metplus.curriculum.database.domain.MetaData;
import org.metplus.curriculum.database.domain.MetaDataField;
import org.metplus.curriculum.database.domain.Resume;
import org.metplus.curriculum.database.repository.ResumeRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Created by joao on 3/21/16.
 */
@RunWith(Suite.class)
@SuiteClasses({ResumeMatcherImplTest.Match.class})
public class ResumeMatcherImplTest {
    @RunWith(MockitoJUnitRunner.class)
    public static class Match{
        @Mock
        private ResumeRepository resumeRepository;

        private CruncherImpl cruncher;

        private ResumeMatcherImpl resumeMatcher;

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
            resumeMatcher = new ResumeMatcherImpl(cruncher, resumeRepository);

            assertEquals(0, resumeMatcher.match("pico", "pico de gallo").size());
        }

        @Test
        public void foundTwo() {

            Mockito.when(resumeRepository.resumesOnCriteria(Mockito.any())).thenReturn(initialize());
            resumeMatcher = new ResumeMatcherImpl(cruncher, resumeRepository);
            List<Resume> result = resumeMatcher.match("ipsum", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean quis odio ut neque venenatis iaculis nec eu diam. Sed a libero odio. Suspendisse iaculis velit nec sodales fermentum. Suspendisse potenti. Donec ultricies nulla vitae facilisis tempor. Cras et pretium augue. Maecenas viverra risus nibh, vitae faucibus massa fermentum vitae. Donec eget urna nec nisl pretium maximus. ");
            assertEquals(2, result.size());
            assertEquals("user2", result.get(0).getUserId());
            assertEquals("user3", result.get(1).getUserId());
        }

        @Test
        public void foundThree() {

            Mockito.when(resumeRepository.resumesOnCriteria(Mockito.any())).thenReturn(initialize());
            resumeMatcher = new ResumeMatcherImpl(cruncher, resumeRepository);
            List<Resume> result = resumeMatcher.match("ipsum", "fermentum ipsum dolor sit amet, fermentum adipiscing elit. fermentum fermentum quis odio ut neque venenatis iaculis nec eu diam. fermentum Sed a libero odio. Suspendisse iaculis velit nec sodales fermentum. Suspendisse potenti. Donec ultricies nulla vitae facilisis tempor. Cras et pretium augue. Maecenas viverra risus nibh, vitae faucibus massa fermentum vitae. Donec eget urna nec nisl pretium maximus. ");
            assertEquals(3, result.size());
            assertEquals("user2", result.get(0).getUserId());
            assertEquals("user3", result.get(1).getUserId());
            assertEquals("user1", result.get(2).getUserId());
        }
    }
}
