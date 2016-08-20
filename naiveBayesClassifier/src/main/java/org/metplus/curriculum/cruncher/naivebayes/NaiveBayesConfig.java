package org.metplus.curriculum.cruncher.naivebayes;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

/**
 * Created by joao on 8/20/16.
 */
@ConfigurationProperties(locations = "classpath:naiveBayes.yml", prefix = "naiveConfig")
@Configuration
public class NaiveBayesConfig {
    private Map<String, List<String>> database;
    private List<String> cleanExpressions;

    public Map<String, List<String>> getDatabase() {
        return database;
    }

    public void setDatabase(Map<String, List<String>> database) {
        this.database = database;
    }

    public List<String> getCleanExpressions() {
        return cleanExpressions;
    }

    public void setCleanExpressions(List<String> cleanExpressions) {
        this.cleanExpressions = cleanExpressions;
    }
}
