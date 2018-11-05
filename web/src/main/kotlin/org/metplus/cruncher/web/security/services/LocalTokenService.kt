package org.metplus.cruncher.web.security.services


import com.fasterxml.jackson.annotation.ObjectIdGenerators
import org.slf4j.LoggerFactory
import java.time.Clock
import java.time.Instant
import java.util.*

class LocalTokenService : TokenService {

    private val timeoutSeconds: Int
    private val tokens = HashMap<UUID, TokenInformation>()
    private val generator = ObjectIdGenerators.UUIDGenerator()

    private var clock: Clock? = null

    constructor() {
        this.timeoutSeconds = 1800
        clock = Clock.systemDefaultZone()
    }

    constructor(timeoutSeconds: Int) {
        this.timeoutSeconds = timeoutSeconds
        clock = Clock.systemDefaultZone()
    }

    constructor(clock: Clock, timeoutSeconds: Int) {
        this.timeoutSeconds = timeoutSeconds
        this.clock = clock
    }

    @Synchronized
    override fun isValid(token: String?): Boolean {
        logger.trace("Checking is token: '{}' is valid", token)

        if (token == null)
            return false

        var tokenUUID: UUID? = null
        try {
            tokenUUID = UUID.fromString(token)
        } catch (exp: IllegalArgumentException) {
            logger.warn("Token '{}' is not a UUID", token)
            return false
        }

        if (tokens.containsKey(tokenUUID)) {
            if (clock!!.instant().compareTo(tokens[tokenUUID]!!.entryDate.plusSeconds(timeoutSeconds.toLong())) < 0)
                return true
            else
                logger.info("Token '{}' for ip '{}' expired", token, tokens[tokenUUID]!!.ipAddress)
        } else
            logger.info("Token '{}' is invalid", token)
        return false
    }

    @Synchronized
    override fun generateToken(ipAddress: String): String {
        logger.trace("generateToken({})", ipAddress)

        for (token in tokens.keys)
            if (tokens[token]!!.ipAddress.compareTo(ipAddress) == 0)
                if (isValid(token.toString())) {
                    logger.debug("Token for ip '{}' is still valid, no need to regenerate", ipAddress)
                    return token.toString()
                } else {
                    logger.debug("Token for ip '{}' is no longer valid, need to generate a new one", ipAddress)
                    tokens.remove(token)
                    break
                }
        val token = generator.generateId(ipAddress)
        logger.info("Generated token '{}' valid for '{}' seconds", token.toString(), timeoutSeconds)
        tokens[token] = TokenInformation(ipAddress, clock!!.instant())
        return token.toString()
    }

    @Synchronized
    override fun totalNumberTokens(): Int {
        return tokens.size
    }

    internal inner class TokenInformation(val ipAddress: String, val entryDate: Instant)

    companion object {
        private val logger = LoggerFactory.getLogger(LocalTokenService::class.java)
    }
}
