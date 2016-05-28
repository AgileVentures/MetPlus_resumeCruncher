package org.metplus.curriculum.cruncher.naivebayes;

import de.daslaboratorium.machinelearning.classifier.bayes.BayesClassifier;
import org.metplus.curriculum.cruncher.Cruncher;
import org.metplus.curriculum.cruncher.CruncherMetaData;

import java.util.List;
import java.util.Map;

/**
 * Created by joao on 5/28/16.
 */
public class CruncherImpl implements Cruncher {
    public static final String CRUNCHER_NAME = "NaiveBayes";

    private BayesClassifier<String, String> classifier;

    public CruncherImpl() {
        classifier = new BayesClassifier();
    }

    public CruncherMetaData crunch(String data) {
        return null;
    }

    public String getCruncherName() {
        return CRUNCHER_NAME;
    }

    public void resetMemory() {
        classifier.reset();
    }

    public void train(Map<String, List<String>> database) {
        for(Map.Entry<String, List<String>> entry: database.entrySet()) {
            train(entry.getKey(), entry.getValue());
        }
    }

    public void train(String feature, List<String> trainningSet) {
        classifier.learn(feature, trainningSet);
    }
}
