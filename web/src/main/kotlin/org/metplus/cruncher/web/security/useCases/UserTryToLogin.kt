package org.metplus.cruncher.web.security.useCases


import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value

class UserTryToLogin(@field:Value("\${backend.admin.username}")
                     private val username: String?, @field:Value("\${backend.admin.password}")
                     private val password: String?) {

    fun canUserLogin(username: String?, password: String?): Boolean {
        logger.trace("canUserLogin({}, {})", username, password)
        if (isUsernameCorrect(username) && isPasswordCorrect(password)) {
            logger.info("User '{}' login was successfull", username)
            return true
        }
        logger.info("Unable to login user '{}'", username)
        return false
    }

    private fun isPasswordCorrect(password: String?): Boolean {
        return password != null && password == this.password
    }

    private fun isUsernameCorrect(username: String?): Boolean {
        return username != null && username == this.username
    }

    companion object {
        private val logger = LoggerFactory.getLogger(UserTryToLogin::class.java)
    }
}
