package org.metplus.curriculum.cruncher.naivebayes;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Created by joao on 8/20/16.
 */
@Component
@ConfigurationProperties(locations = {"classpath:naiveBayes.yml"}, prefix = "config")
public class NaiveBayesConfig {
    private Map<String, List<String>> learnDatabase;
    private List<String> cleanExpressions;

    public Map<String, List<String>> getLearnDatabase() {
        return learnDatabase;
    }

    public void setLearnDatabase(Map<String, List<String>> learnDatabase) {
        this.learnDatabase = learnDatabase;
    }

    public List<String> getCleanExpressions() {
        return cleanExpressions;
    }

    public void setCleanExpressions(List<String> cleanExpressions) {
        this.cleanExpressions = cleanExpressions;
    }
}
