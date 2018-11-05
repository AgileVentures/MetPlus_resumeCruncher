package org.metplus.cruncher.web.security.services

interface TokenService {
    fun isValid(token: String?): Boolean

    fun generateToken(ipAddress: String): String

    fun totalNumberTokens(): Int
}
