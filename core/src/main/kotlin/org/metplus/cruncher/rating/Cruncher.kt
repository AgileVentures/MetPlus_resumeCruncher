package org.metplus.cruncher.rating


data class CruncherMetaData(
        val metaData: HashMap<String, Double>
)

interface Cruncher {
    fun crunch(data: String): CruncherMetaData
    fun getCruncherName(): String
}

open class CruncherList {
    private var allCrunchers = mutableListOf<Cruncher>()

    fun addCruncher(cruncher: Cruncher) {
        allCrunchers.add(cruncher)
    }

    fun getCrunchers(): List<Cruncher> {
        return allCrunchers
    }
}