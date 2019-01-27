package org.metplus.cruncher.job

import org.metplus.cruncher.rating.CruncherMetaData

data class Job(
        val id: String,
        val title: String,
        val description: String,
        val titleMetaData: Map<String, CruncherMetaData>,
        val descriptionMetaData: Map<String, CruncherMetaData>,
        val starRating: Double = 0.0
)