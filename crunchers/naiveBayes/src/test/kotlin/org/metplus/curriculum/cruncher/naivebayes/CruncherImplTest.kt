package org.metplus.curriculum.cruncher.naivebayes


import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class CruncherImplTest {
    class TrainWithMap {
        private lateinit var cruncher: CruncherImpl

        @BeforeEach
        fun before() {
            this.cruncher = CruncherImpl()
        }

        @Test
        fun emptySet() {
            cruncher.train(emptyMap())
            assertThat(cruncher.classifier.categories.size).isEqualTo(0)
        }

        @Test
        fun oneFeature() {
            val database = HashMap<String, List<String>>()
            val feature = ArrayList<String>()
            feature.add("nice")
            feature.add("good")
            feature.add("weather")

            database["bamm"] = feature
            cruncher.train(database)
            assertThat(cruncher.classifier.categories.size).isEqualTo(1)
        }

        @Test
        fun twoFeatures() {
            val database = HashMap<String, List<String>>()
            val feature = ArrayList<String>()
            feature.add("nice")
            feature.add("good")
            feature.add("weather")

            database["bamm"] = feature
            database["bamm1"] = feature
            cruncher.train(database)
            assertThat(cruncher.classifier.categories.size).isEqualTo(2)
        }
    }

    class TrainSingleFeature {
        private lateinit var cruncher: CruncherImpl

        @BeforeEach
        fun before() {
            this.cruncher = CruncherImpl()
        }

        @Test
        fun nullSet() {
            cruncher.train("bamm", null)
            assertThat(cruncher.classifier.categoriesTotal).isEqualTo(0)
        }

        @Test
        fun emptySet() {
            cruncher.train("bamm", emptyList())
            assertThat(cruncher.classifier.categoriesTotal).isEqualTo(0)
        }

        @Test
        fun oneFeature() {
            val feature = ArrayList<String>()
            feature.add("nice")
            feature.add("good")
            feature.add("weather")

            cruncher.train("bamm", feature)
            assertThat(cruncher.classifier.categories.size).isEqualTo(1)
        }
    }

    class Crunch {
        private lateinit var cruncher: CruncherImpl

        @BeforeEach
        fun before() {
            this.cruncher = CruncherImpl()
            cruncher.train(mapOf(
                    "positive" to listOf("I love sunny days"),
                    "negative" to listOf("I hate rain")
            ))
        }

        @Test
        fun checkCruncherResults() {
            val unknownText1 = "today is a sunny day"
            val unknownText2 = "there will be rain"

            assertThat(cruncher.crunch(unknownText1).metaData["positive"]).isGreaterThan(cruncher.crunch(unknownText1).metaData["negative"])
            assertThat(cruncher.crunch(unknownText2).metaData["negative"]).isGreaterThan(cruncher.crunch(unknownText2).metaData["positive"])
        }
    }
}
