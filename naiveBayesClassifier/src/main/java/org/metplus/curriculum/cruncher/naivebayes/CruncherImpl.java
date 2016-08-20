package org.metplus.curriculum.cruncher.naivebayes;

import de.daslaboratorium.machinelearning.classifier.Classification;
import de.daslaboratorium.machinelearning.classifier.bayes.BayesClassifier;
import org.metplus.curriculum.cruncher.Cruncher;
import org.metplus.curriculum.cruncher.CruncherMetaData;
import org.metplus.curriculum.database.domain.MetaDataField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by joao on 5/28/16.
 */
public class CruncherImpl implements Cruncher {
    private static final Logger logger = LoggerFactory.getLogger(CruncherImpl.class);
    public static final String CRUNCHER_NAME = "NaiveBayes";

    private BayesClassifier<String, String> classifier;

    public void setCleanExpressions(List<String> cleanExpressions) {
        this.cleanExpressions = cleanExpressions;
    }

    private List<String> cleanExpressions;

    public CruncherImpl(List<String> cleanExpressions) {
        classifier = new BayesClassifier();
        this.cleanExpressions = cleanExpressions;
    }

    public CruncherMetaData crunch(String data) {
        logger.trace("crunch(" + data + ")");
        Collection<Classification<String, String>> results = classifier.classifyDetailed(tokenize(data));
        NaiveBayesMetaData metaData = generateMetaData(results);
        return metaData;
    }
    private NaiveBayesMetaData generateMetaData(Collection<Classification<String, String>> result) {
        logger.trace("generateMetaData(" + result + ")");
        NaiveBayesMetaData allMetaData = new NaiveBayesMetaData();
        allMetaData.setBestMatchCategory(null);
        String output = "Cruncher categories: [";
        for(Classification<String, String> metaData: result) {
            if(allMetaData.getBestMatchCategory() == null)
                allMetaData.setBestMatchCategory(metaData.getCategory());
            if(metaData.getProbability() <= 0)
                break;
            MetaDataField<Float> field = new MetaDataField<>(metaData.getProbability());
            allMetaData.addField(metaData.getCategory(), field);
            output += ("" + metaData.getCategory() + ": " + metaData.getProbability() + ", ");
        }
        logger.debug(output + "]");
        return allMetaData;
    }

    public String getCruncherName() {
        return CRUNCHER_NAME;
    }

    public void resetMemory() {
        classifier.reset();
    }

    public void train(Map<String, List<String>> database) {
        logger.trace("train(" + database + ")");
        if(database != null && database.size() > 0 ) {
            logger.info("Training using a database with {} categories", database.size());
            for (Map.Entry<String, List<String>> entry : database.entrySet()) {
                train(entry.getKey(), entry.getValue());
            }
        }
        logger.info("Train complete");
    }
    public Set<String> getCategories() {
        return classifier.getCategories();
    }

    public void train(String feature, List<String> trainningSet) {
        for(String text: trainningSet)
            classifier.learn(feature, tokenize(feature, text));
    }

    private List<String> tokenize(String text) {
        return tokenize("", text);
    }
    private List<String> tokenize(String feature, String text) {
        List<String> result = new ArrayList<>();
        String newText = text.replaceAll(feature, feature.replaceAll("\\s+", "@@@@")).toLowerCase();
        for(String expression: newText.split("\\s")) {
            if(expression.length() == 0)
                continue;
            String token = expression.replaceAll("@@@@", " ").replaceAll("[^a-z0-9 ]", "");
            result.add(token);
        }
        return result;
    }
}
