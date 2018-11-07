package org.metplus.cruncher.resume

import java.io.InputStream

data class ResumeFile(
        val filename: String,
        val userId: String,
        val fileStream: InputStream
)
