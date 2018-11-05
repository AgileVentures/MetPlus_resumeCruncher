package org.metplus.cruncher.web.security.useCases

import org.metplus.cruncher.web.security.services.TokenService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired

class UserTokenAuthentication(@field:Autowired
                              private val tokenService: TokenService) {

    fun canLogin(token: String?): Boolean {
        logger.trace("canLogin($token)")

        if (tokenService.isValid(token)) {
            logger.debug("Login successfull with token: $token")
            return true
        }
        logger.debug("Token '$token' is not valid")
        return false
    }

    companion object {
        private val logger = LoggerFactory.getLogger(UserTokenAuthentication::class.java)
    }
}
