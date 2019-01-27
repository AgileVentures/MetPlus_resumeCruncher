package org.metplus.cruncher.rating


data class CruncherMetaData(
        val metaData: MutableMap<String, Double>
)

interface Cruncher {
    fun crunch(data: String): CruncherMetaData
    fun getCruncherName(): String
    fun train(database: Map<String, List<String>>)
}

open class CruncherList(
        private var allCrunchers: List<Cruncher> = mutableListOf()
) {
    fun getCrunchers(): List<Cruncher> {
        return allCrunchers
    }
}

open class MatcherList(
        private var allMatchers: List<Matcher<*, *>> = mutableListOf()
) {
    fun getMatchers(): List<Matcher<*, *>> {
        return allMatchers
    }
}