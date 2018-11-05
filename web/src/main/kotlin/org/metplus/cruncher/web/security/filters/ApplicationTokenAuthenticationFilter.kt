package org.metplus.cruncher.web.security.filters


import org.metplus.cruncher.web.security.services.TokenService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class ApplicationTokenAuthenticationFilter(@field:Autowired
                                           internal var tokenService: TokenService) : HandlerInterceptorAdapter() {
    @Throws(Exception::class)
    override fun preHandle(request: HttpServletRequest?, response: HttpServletResponse?, handler: Any?): Boolean {
        val token = request!!.getHeader("X-Auth-Token")
        logger.debug("Request from '{}'", request.remoteHost)

        if (tokenService.isValid(token))
            return true

        logger.info("Request from '{}' with invalid token '{}' denied", request.remoteAddr, token)
        response!!.status = 401
        return false
    }

    companion object {
        private val logger = LoggerFactory.getLogger(ApplicationTokenAuthenticationFilter::class.java)
    }
}
