package org.metplus.cruncher.web.controller


enum class ResultCodes {
    JOB_ID_EXISTS,
    SUCCESS,
    JOB_NOT_FOUND,
    FATAL_ERROR,
    RESUME_NOT_FOUND
}

open class CruncherResponse(
        val resultCode: ResultCodes,
        val message: String)