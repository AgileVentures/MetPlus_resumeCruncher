package org.metplus.curriculum.cruncher.naivebayes

import de.daslaboratorium.machinelearning.classifier.Classification
import de.daslaboratorium.machinelearning.classifier.Classifier
import java.util.Comparator
import java.util.SortedSet
import java.util.TreeSet

/**
 * @param <T> The feature class.
 * @param <K> The category class.
</K></T> */
class BayesClassifierWithSmoothing<T, K> : Classifier<T, K>() {

    /**
     * Calculates the product of all feature probabilities: PROD(P(featI|cat)
     *
     * @param features The set of features to use.
     * @param category The category to test for.
     * @return The product of all feature probabilities.
     */
    private fun featuresProbabilityProduct(features: Collection<T>,
                                           category: K): Float {
        var product = 1.0f
        for (feature in features)
            product *= this.featureWeighedAverage(feature, category, de.daslaboratorium.machinelearning.classifier.IFeatureProbability { feature, category ->
                if (this@BayesClassifierWithSmoothing.categoryCount(category) == 0)
                    return@IFeatureProbability 0f
                val totalFeatureFoundInBrain = this@BayesClassifierWithSmoothing.featureCount(feature)
                (this@BayesClassifierWithSmoothing.featureCount(feature, category) + totalFeatureFoundInBrain).toFloat() / (this@BayesClassifierWithSmoothing.categoryCount(category).toFloat() + 1)
            })
        return product
    }

    /**
     * Calculates the probability that the features can be classified as the
     * category given.
     *
     * @param features The set of features to use.
     * @param category The category to test for.
     * @return The probability that the features can be classified as the
     * category.
     */
    private fun categoryProbability(features: Collection<T>, category: K): Float {
        return this.categoryCount(category).toFloat() / this.categoriesTotal.toFloat() * featuresProbabilityProduct(features, category)
    }

    /**
     * Retrieves a sorted `Set` of probabilities that the given set
     * of features is classified as the available categories.
     *
     * @param features The set of features to use.
     * @return A sorted `Set` of category-probability-entries.
     */
    private fun categoryProbabilities(
            features: Collection<T>): SortedSet<Classification<T, K>> {

        /*
         * Sort the set according to the possibilities. Because we have to sort
         * by the mapped value and not by the mapped key, we can not use a
         * sorted tree (TreeMap) and we have to use a set-entry approach to
         * achieve the desired functionality. A custom comparator is therefore
         * needed.
         */
        val probabilities = TreeSet(
                Comparator<Classification<T, K>> { o1, o2 ->
                    var toReturn = java.lang.Float.compare(
                            o1.probability, o2.probability)
                    if (toReturn == 0 && o1.category != o2.category)
                        toReturn = -1
                    toReturn
                })

        for (category in this.categories)
            probabilities.add(Classification(
                    features, category,
                    this.categoryProbability(features, category)))
        return probabilities
    }

    /**
     * Classifies the given set of features.
     *
     * @return The category the set of features is classified as.
     */
    override fun classify(features: Collection<T>): Classification<T, K>? {
        val probabilites = this.categoryProbabilities(features)

        return if (probabilites.size > 0) {
            probabilites.last()
        } else null
    }

    /**
     * Classifies the given set of features. and return the full details of the
     * classification.
     *
     * @return The set of categories the set of features is classified as.
     */
    fun classifyDetailed(
            features: Collection<T>): Collection<Classification<T, K>> {
        return this.categoryProbabilities(features)
    }

}
