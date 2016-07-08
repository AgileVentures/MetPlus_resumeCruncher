package org.metplus.curriculum.cruncher.naivebayes;

import de.daslaboratorium.machinelearning.classifier.Classification;
import de.daslaboratorium.machinelearning.classifier.bayes.BayesClassifier;
import org.metplus.curriculum.cruncher.Cruncher;
import org.metplus.curriculum.cruncher.CruncherMetaData;
import org.metplus.curriculum.database.domain.MetaDataField;

import java.util.Arrays;
import java.util.Collection;
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
        Collection<Classification<String, String>> results = classifier.classifyDetailed(Arrays.asList(data));
        NaiveBayesMetaData metaData = generateMetaData(results);
        return metaData;
    }
    private NaiveBayesMetaData generateMetaData(Collection<Classification<String, String>> result) {
        NaiveBayesMetaData allMetaData = new NaiveBayesMetaData();
        allMetaData.setBestMatchCategory(null);
        for(Classification<String, String> metaData: result) {
            if(allMetaData.getBestMatchCategory() == null)
                allMetaData.setBestMatchCategory(metaData.getCategory());
            if(metaData.getProbability() <= 0)
                break;
            MetaDataField<Float> field = new MetaDataField<>(metaData.getProbability());
            allMetaData.addField(metaData.getCategory(), field);
        }
        return allMetaData;
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
