package org.metplus.cruncher.job

import org.metplus.cruncher.rating.CruncherMetaData

data class Job(
        val id: String,
        val title: String,
        val description: String,
        val titleMetaData: CruncherMetaData,
        val descriptionMetaData: CruncherMetaData,
        val starRating: Double = 0.0
)