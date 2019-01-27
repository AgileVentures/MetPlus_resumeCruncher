package org.metplus.cruncher.resume

import org.metplus.cruncher.rating.CruncherMetaData

data class Resume(
        val filename: String,
        val userId: String,
        val fileType: String,
        val cruncherData: Map<String, CruncherMetaData>,
        val starRating: Double = 0.0
)
