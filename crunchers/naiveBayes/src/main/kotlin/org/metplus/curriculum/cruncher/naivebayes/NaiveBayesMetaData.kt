package org.metplus.curriculum.cruncher.naivebayes

import org.metplus.cruncher.rating.CruncherMetaData

class NaiveBayesMetaData {
    var metaData = CruncherMetaData(mutableMapOf())

    var bestMatchCategory: String? = null

    var totalProbability = 0.0

    fun addToTotalProbability(probability: Double) {
        totalProbability += probability
    }

    /**
     * Function used to add a new category to the data
     * @param category Name of the category
     * @param probability Probability
     */
    fun addCategory(category: String, probability: Double) {
        metaData.metaData[category] = probability
    }
}