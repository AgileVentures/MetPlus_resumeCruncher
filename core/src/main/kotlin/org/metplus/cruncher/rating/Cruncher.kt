package org.metplus.cruncher.rating

interface CruncherMetaData

interface Cruncher {
    fun crunch(data: String): CruncherMetaData
    fun getCruncherName(): String
}