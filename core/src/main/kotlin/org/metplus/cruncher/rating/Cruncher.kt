package org.metplus.cruncher.rating

data class CruncherMetaData(
        val metaData: HashMap<String, Long>
)

interface Cruncher {
    fun crunch(data: String): CruncherMetaData
    fun getCruncherName(): String
}