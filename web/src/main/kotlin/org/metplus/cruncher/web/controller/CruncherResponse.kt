package org.metplus.cruncher.web.controller


enum class ResultCodes {
    JOB_ID_EXISTS,
    SUCCESS,
    JOB_NOT_FOUND,
    FATAL_ERROR
}

data class CruncherResponse(
        val resultCode: ResultCodes,
        val message: String)