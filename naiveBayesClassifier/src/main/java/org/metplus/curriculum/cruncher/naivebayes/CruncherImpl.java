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
 * Class that implements the Naive Bayes cruncher
 */
public class CruncherImpl implements Cruncher {
    private static final Logger logger = LoggerFactory.getLogger(CruncherImpl.class);
    public static final String CRUNCHER_NAME = "NaiveBayes";

    private BayesClassifier<String, String> classifier;

    public void setCleanExpressions(List<String> cleanExpressions) {
        this.cleanExpressions = cleanExpressions;
    }
    public BayesClassifier<String, String> getClassifier() {
        return classifier;
    }

    private List<String> cleanExpressions;

    public CruncherImpl() {
        classifier = new BayesClassifier();
        this.cleanExpressions = new ArrayList<>();
    }
    public CruncherImpl(List<String> cleanExpressions) {
        classifier = new BayesClassifier();
        this.cleanExpressions = cleanExpressions;
    }

    /**
     * Crunch a string
     * @param data Input to be processed
     * @return Cruncher meta data
     */
    public CruncherMetaData crunch(String data) {
        logger.trace("crunch(" + data + ")");
        Collection<Classification<String, String>> results = classifier.classifyDetailed(tokenize(data));
        NaiveBayesMetaData metaData = generateMetaData(results);
        return metaData;
    }

    /**
     * Generate the meta data needed for a collection of classifications
     * @param classificationResult Result of a classification
     * @return Meta data ready to be saved
     */
    private NaiveBayesMetaData generateMetaData(Collection<Classification<String, String>> classificationResult) {
        logger.trace("generateMetaData(" + classificationResult + ")");
        NaiveBayesMetaData allMetaData = new NaiveBayesMetaData();
        float maxProbability = 0.0f;
        allMetaData.setBestMatchCategory(null);
        String output = "Cruncher categories: [";
        for(Classification<String, String> metaData: classificationResult) {
            if(allMetaData.getBestMatchCategory() == null)
                allMetaData.setBestMatchCategory(metaData.getCategory());
            else if(maxProbability < metaData.getProbability()) {
                allMetaData.setBestMatchCategory(metaData.getCategory());
                maxProbability = metaData.getProbability();
            }
            output += ("" + metaData.getCategory() + ": " + metaData.getProbability() + ", ");

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

    /**
     * Reset the memory of the classifier
     */
    public void resetMemory() {
        classifier.reset();
    }

    /**
     * Train the cuncher
     * @param database Database with all the features and their tokens
     */
    public void train(Map<String, List<String>> database) {
        logger.trace("train(" + database + ")");
        if(database != null && database.size() > 0 ) {
            logger.debug("Training using a database with {} categories", database.size());
            for (Map.Entry<String, List<String>> entry : database.entrySet()) {
                train(entry.getKey(), entry.getValue());
            }
        }
        logger.info("Train complete");
    }

    /**
     * Retrieve all the categories of the classifier
     * @return Set with category names
     */
    public Set<String> getCategories() {
        return classifier.getCategories();
    }

    /**
     * Train the cruncher
     * @param feature Feature name
     * @param trainningSet Set of tokens that represent a feature
     */
    public void train(String feature, List<String> trainningSet) {
        if(trainningSet == null)
            return;
        for(String text: trainningSet)
            classifier.learn(feature, tokenize(feature, text));
    }

    /**
     * Function used to tokenize a string
     * @param text To be tokenized
     * @return List of tokens from the text
     */
    private List<String> tokenize(String text) {
        return tokenize("", text);
    }

    /**
     * Tokenize using a feature
     * @param feature find a specific feature and save it as a token
     * @param text Text to be tokenized
     * @return List of tokens from the text
     */
    private List<String> tokenize(String feature, String text) {
        List<String> result = new ArrayList<>();
        String newText = text.replaceAll(feature, feature.replaceAll("\\s+", "@@@@")).toLowerCase();
        if(cleanExpressions.size() > 0) {
            for(String exp: cleanExpressions) {
                newText = newText.replaceAll("\\b" + exp + "\\b", " ");
            }
        }
        for(String expression: newText.split("\\s")) {
            if(expression.length() == 0)
                continue;
            String token = expression.replaceAll("@@@@", " ").replaceAll("[^a-z0-9 ]", "");
            result.add(token);
        }
        return result;
    }
}
