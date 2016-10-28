package org.metplus.curriculum.cruncher.naivebayes;

import org.metplus.curriculum.database.domain.MetaData;
import org.metplus.curriculum.database.domain.MetaDataField;

/**
 * Created by jpereira on 7/8/2016.
 */
public class NaiveBayesMetaData extends MetaData {
    public String getBestMatchCategory() {
        return bestMatchCategory;
    }

    public void setBestMatchCategory(String bestMatchCategory) {
        this.bestMatchCategory = bestMatchCategory;
    }

    private String bestMatchCategory;

    private double totalProbability = 0.f;

    public void addToTotalProbability(double probability) {
        totalProbability += probability;
    }
    public void setTotalProbability(double probability) {
        totalProbability = probability;
    }
    public double getTotalProbability() {
        return totalProbability;
    }

    /**
     * Function used to add a new category to the data
     * @param category Name of the category
     * @param probability Probability
     */
    public void addCategory(String category, double probability) {
        this.addField(category, new MetaDataField(probability));
    }
}
