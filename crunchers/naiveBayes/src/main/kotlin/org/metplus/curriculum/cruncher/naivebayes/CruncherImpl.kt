package org.metplus.curriculum.cruncher.naivebayes

import de.daslaboratorium.machinelearning.classifier.Classification
import de.daslaboratorium.machinelearning.classifier.bayes.BayesClassifier
import org.metplus.cruncher.rating.Cruncher
import org.metplus.cruncher.rating.CruncherMetaData
import org.slf4j.LoggerFactory
import java.util.ArrayList

class CruncherImpl(
        private var cleanExpressions: List<String> = mutableListOf()
) : Cruncher {
    private val logger = LoggerFactory.getLogger(CruncherImpl::class.java)
    private val name = "NaiveBayes"
    val classifier = BayesClassifier<String, String>()

    override fun crunch(data: String): CruncherMetaData {
        logger.trace("crunch($data)")
        val results = classifier.classifyDetailed(tokenize(data))
        return generateMetaData(results).metaData
    }

    private fun generateMetaData(classificationResult: Collection<Classification<String, String>>): NaiveBayesMetaData {
        logger.trace("generateMetaData($classificationResult)")
        val allMetaData = NaiveBayesMetaData()
        var maxProbability = 0.0f
        allMetaData.bestMatchCategory = null
        var output = "Cruncher categories: ["
        for (metaData in classificationResult) {
            if (allMetaData.bestMatchCategory == null)
                allMetaData.bestMatchCategory = metaData.category
            else if (maxProbability < metaData.probability) {
                allMetaData.bestMatchCategory = metaData.category
                maxProbability = metaData.probability
            }

            if (metaData.probability <= 0)
                continue

            allMetaData.metaData.metaData[metaData.category] = metaData.probability.toDouble()
            output += "" + metaData.category + ": " + metaData.probability + ", "
        }
        allMetaData.totalProbability = maxProbability.toDouble()
        logger.debug(output + "], Best Match: " + allMetaData.bestMatchCategory + ": " + allMetaData.totalProbability)
        return allMetaData
    }

    override fun getCruncherName(): String {
        return name
    }

    fun train(database: Map<String, List<String>>) {
        logger.trace("train($database)")
        if (database.isNotEmpty()) {
            logger.debug("Training using a database with {} categories", database.size)
            for (entry in database.entries) {
                train(entry.key, entry.value)
            }
        }
        logger.info("Train complete")
    }

    fun train(feature: String, trainningSet: List<String>?) {
        if (trainningSet == null)
            return
        for (text in trainningSet)
            classifier.learn(feature, tokenize(feature, text))
    }

    private fun tokenize(text: String): List<String> {
        return tokenize("", text)
    }

    private fun tokenize(feature: String, text: String): List<String> {
        val result = ArrayList<String>()
        var newText = text.replace(feature.toRegex(), feature.replace("\\s+".toRegex(), "@@@@")).toLowerCase()
        if (cleanExpressions.isNotEmpty()) {
            for (exp in cleanExpressions) {
                newText = newText.replace("\\b$exp\\b".toRegex(), " ")
            }
        }
        for (expression in newText.split("\\s".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
            if (expression.trim { it <= ' ' }.isEmpty())
                continue
            val token = expression.replace("@@@@".toRegex(), " ").replace("[^a-z0-9 ]".toRegex(), "")
            if (token.trim { it <= ' ' }.isEmpty())
                continue
            result.add(token)
        }
        return result
    }
}