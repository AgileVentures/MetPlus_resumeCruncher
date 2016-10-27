package org.metplus.curriculum.cruncher.naivebayes;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.metplus.curriculum.database.domain.MetaData;
import org.metplus.curriculum.database.domain.MetaDataField;
import org.metplus.curriculum.database.domain.Resume;
import org.metplus.curriculum.database.repository.JobRepository;
import org.metplus.curriculum.database.repository.ResumeRepository;
import org.metplus.curriculum.test.BeforeAfterInterface;
import org.metplus.curriculum.test.BeforeAfterRule;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Created by joaopereira on 10/27/2016.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({MatcherImplTest.MatchProbability_TwoLists.class,
                     MatcherImplTest.MatchProbability_ListAndMetaData.class})
public class MatcherImplTest {
    @RunWith(MockitoJUnitRunner.class)
    public static class Base implements BeforeAfterInterface {
        @Rule
        public BeforeAfterRule beforeAfter = new BeforeAfterRule(this);
        protected MatcherImpl matcher;
        @Mock
        ResumeRepository resumeRepository;
        @Mock
        JobRepository jobRepository;
        @Mock
        protected CruncherImpl cruncher;

        @Override
        public void after() {

        }

        @Override
        public void before() {
            matcher = new MatcherImpl(cruncher,resumeRepository, jobRepository);
        }
    }

    public static class MatchProbability_TwoLists extends Base {
        List<String> categories1;
        @Override
        public void before() {
            super.before();
            categories1 = new ArrayList<>();
            categories1.add("cat 1");
            categories1.add("cat 2");
            categories1.add("cat 3");
            categories1.add("cat 4");
            categories1.add("cat 5");
        }

        @Test
        public void allMatch() {
            assertEquals(1, matcher.matchProbability(categories1, categories1), 0.01);
        }

        @Test
        public void noMatch() {
            List<String> categories2 = new ArrayList<>();
            categories2.add("");
            categories2.add("");
            categories2.add("");
            categories2.add("");
            categories2.add("");
            assertEquals(0, matcher.matchProbability(categories1, categories2), 0.01);
        }

        @Test
        public void mostRelevantMatchOnly() {
            List<String> categories2 = new ArrayList<>();
            categories2.add("cat 1");
            categories2.add("");
            categories2.add("");
            categories2.add("");
            categories2.add("");
            assertEquals(0.51, matcher.matchProbability(categories1, categories2), 0.01);
        }

        @Test
        public void mostRelevantMatchOnlyMatchOnLastPlace() {
            List<String> categories2 = new ArrayList<>();
            categories2.add("");
            categories2.add("");
            categories2.add("");
            categories2.add("");
            categories2.add("cat 1");
            assertEquals(0.24, matcher.matchProbability(categories1, categories2), 0.01);
        }
        @Test
        public void inverse() {
            List<String> categories2 = new ArrayList<>();
            categories2.add("cat 5");
            categories2.add("");
            categories2.add("");
            categories2.add("");
            categories2.add("");
            assertEquals(0.27, matcher.matchProbability(categories1, categories2), 0.01);
        }
    }
    public static class MatchProbability_ListAndMetaData extends Base {
        List<String> categories1;
        @Override
        public void before() {
            super.before();
            categories1 = new ArrayList<>();
            categories1.add("cat 1");
            categories1.add("cat 2");
            categories1.add("cat 3");
            categories1.add("cat 4");
            categories1.add("cat 5");
        }

        @Test
        public void allMatch() {
            NaiveBayesMetaData data = new NaiveBayesMetaData();
            data.addField("cat 1", new MetaDataField(5.));
            data.addField("cat 2", new MetaDataField(4.));
            data.addField("cat 3", new MetaDataField(3.));
            data.addField("cat 4", new MetaDataField(2.));
            data.addField("cat 5", new MetaDataField(1.));
            assertEquals(1, matcher.matchProbability(categories1, data), 0.01);
        }

        @Test
        public void noMatch() {
            NaiveBayesMetaData data = new NaiveBayesMetaData();
            data.addField("other cat", new MetaDataField(3.));
            assertEquals(-1, matcher.matchProbability(categories1, data), 0);
        }

        @Test
        public void mostRelevantMatchOnly() {
            NaiveBayesMetaData data = new NaiveBayesMetaData();
            data.addField("cat 1", new MetaDataField(3.));
            assertEquals(0.51, matcher.matchProbability(categories1, data), 0.01);
        }

        @Test
        public void mostRelevantMatchOnlyMatchOnLastPlace() {
            NaiveBayesMetaData data = new NaiveBayesMetaData();
            data.addField("other cat 1", new MetaDataField(5.));
            data.addField("other cat 2", new MetaDataField(4.));
            data.addField("other cat 3", new MetaDataField(3.));
            data.addField("other cat 4", new MetaDataField(2.));
            data.addField("cat 1", new MetaDataField(1.));
            assertEquals(0.24, matcher.matchProbability(categories1, data), 0.01);
        }
        @Test
        public void inverse() {
            NaiveBayesMetaData data = new NaiveBayesMetaData();
            data.addField("cat 5", new MetaDataField(6.));
            data.addField("other cat 1", new MetaDataField(5.));
            data.addField("other cat 2", new MetaDataField(4.));
            data.addField("other cat 3", new MetaDataField(3.));
            data.addField("other cat 4", new MetaDataField(2.));
            assertEquals(0.27, matcher.matchProbability(categories1, data), 0.01);
        }
    }


    public static class CompareJobResume extends Base {
        List<String> categories1;
        @Override
        public void before() {
            super.before();
            categories1 = new ArrayList<>();
            categories1.add("cat 1");
            categories1.add("cat 2");
            categories1.add("cat 3");
            categories1.add("cat 4");
            categories1.add("cat 5");
            Resume resume = new Resume();
            NaiveBayesMetaData data = new NaiveBayesMetaData();
            Map<String, MetaData> metaData = new HashMap<>();
            metaData.put(CruncherImpl.CRUNCHER_NAME, data);
            resume.setMetaData(metaData);
        }

        @Test
        public void allMatch() {
            NaiveBayesMetaData data = new NaiveBayesMetaData();
            data.addField("cat 1", new MetaDataField(5.));
            data.addField("cat 2", new MetaDataField(4.));
            data.addField("cat 3", new MetaDataField(3.));
            data.addField("cat 4", new MetaDataField(2.));
            data.addField("cat 5", new MetaDataField(1.));
            assertEquals(1, matcher.matchProbability(categories1, data), 0.01);
        }

        @Test
        public void noMatch() {
            NaiveBayesMetaData data = new NaiveBayesMetaData();
            data.addField("other cat", new MetaDataField(3.));
            assertEquals(-1, matcher.matchProbability(categories1, data), 0);
        }

        @Test
        public void mostRelevantMatchOnly() {
            NaiveBayesMetaData data = new NaiveBayesMetaData();
            data.addField("cat 1", new MetaDataField(3.));
            assertEquals(0.51, matcher.matchProbability(categories1, data), 0.01);
        }

        @Test
        public void mostRelevantMatchOnlyMatchOnLastPlace() {
            NaiveBayesMetaData data = new NaiveBayesMetaData();
            data.addField("other cat 1", new MetaDataField(5.));
            data.addField("other cat 2", new MetaDataField(4.));
            data.addField("other cat 3", new MetaDataField(3.));
            data.addField("other cat 4", new MetaDataField(2.));
            data.addField("cat 1", new MetaDataField(1.));
            assertEquals(0.24, matcher.matchProbability(categories1, data), 0.01);
        }
        @Test
        public void inverse() {
            NaiveBayesMetaData data = new NaiveBayesMetaData();
            data.addField("cat 5", new MetaDataField(6.));
            data.addField("other cat 1", new MetaDataField(5.));
            data.addField("other cat 2", new MetaDataField(4.));
            data.addField("other cat 3", new MetaDataField(3.));
            data.addField("other cat 4", new MetaDataField(2.));
            assertEquals(0.27, matcher.matchProbability(categories1, data), 0.01);
        }
    }
}
