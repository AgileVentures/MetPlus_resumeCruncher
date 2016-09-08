package org.metplus.curriculum.cruncher.naivebayes;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.metplus.curriculum.test.BeforeAfterInterface;
import org.metplus.curriculum.test.BeforeAfterRule;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by joao on 9/8/16.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({CruncherImplTest.TrainWithMap.class,
        CruncherImplTest.TrainSingleFeature.class})
public class CruncherImplTest {
    public static class Base implements BeforeAfterInterface {
        @Rule
        public BeforeAfterRule beforeAfter = new BeforeAfterRule(this);
        protected CruncherImpl cruncher;
        @Override
        public void after() {

        }

        @Override
        public void before() {
            cruncher = new CruncherImpl();
        }
    }

    public static class TrainWithMap extends Base {
        @Test
        public void nullSet() {
            cruncher.train(null);
            assertEquals(0, cruncher.getClassifier().getCategoriesTotal());
        }
        @Test
        public void emptySet() {
            cruncher.train(new HashMap<>());
            assertEquals(0, cruncher.getClassifier().getCategories().size());
        }

        @Test
        public void oneFeature() {
            HashMap<String, List<String>> database = new HashMap<>();
            ArrayList<String> feature = new ArrayList<>();
            feature.add("nice");
            feature.add("good");
            feature.add("weather");

            database.put("bamm", feature);
            cruncher.train(database);
            assertEquals(1, cruncher.getClassifier().getCategories().size());
        }

        @Test
        public void twoFeatures() {
            HashMap<String, List<String>> database = new HashMap<>();
            ArrayList<String> feature = new ArrayList<>();
            feature.add("nice");
            feature.add("good");
            feature.add("weather");

            database.put("bamm", feature);
            database.put("bamm1", feature);
            cruncher.train(database);
            assertEquals(2, cruncher.getClassifier().getCategories().size());
        }
    }

    public static class TrainSingleFeature extends Base {
        @Test
        public void nullSet() {
            cruncher.train("bamm", null);
            assertEquals(0, cruncher.getClassifier().getCategoriesTotal());
        }
        @Test
        public void emptySet() {
            cruncher.train("bamm", new ArrayList<>());
            assertEquals(0, cruncher.getClassifier().getCategoriesTotal());
        }
        @Test
        public void oneFeature() {
            ArrayList<String> feature = new ArrayList<>();
            feature.add("nice");
            feature.add("good");
            feature.add("weather");

            cruncher.train("bamm", feature);
            assertEquals(1, cruncher.getClassifier().getCategories().size());
        }
    }
}
