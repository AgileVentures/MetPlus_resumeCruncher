package org.metplus.cruncher.web.security.filters

import org.metplus.cruncher.web.security.services.TokenService
import org.metplus.cruncher.web.security.useCases.UserTryToLogin
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class ApplicationLoginFilter(@field:Autowired
                             private val loginUseCase: UserTryToLogin, @field:Autowired
                             private val tokenService: TokenService) : HandlerInterceptorAdapter() {

    @Throws(Exception::class)
    override fun preHandle(request: HttpServletRequest?, response: HttpServletResponse?, handler: Any?): Boolean {
        val username = request!!.getHeader("X-Auth-Username")
        val password = request.getHeader("X-Auth-Password")

        if (loginUseCase.canUserLogin(username, password)) {
            logger.info("Login successful for '{}'", username)
            response!!.status = 200
            response.outputStream.print("{\"token\": \"" + tokenService.generateToken(request.remoteAddr) + "\"}")

            response.outputStream.flush()
            response.contentType = "application/json"
        } else {
            logger.info("Invalid credentials on request from '{}'", request.remoteAddr)
            response!!.status = 401
        }
        return false
    }

    companion object {
        private val logger = LoggerFactory.getLogger(ApplicationLoginFilter::class.java)
    }
}
