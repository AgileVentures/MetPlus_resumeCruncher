package org.metplus.curriculum.security.filters;

import org.metplus.curriculum.security.services.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ApplicationTokenAuthenticationFilter  extends HandlerInterceptorAdapter {
    private static Logger logger = LoggerFactory.getLogger(ApplicationTokenAuthenticationFilter.class);

    @Autowired
    TokenService tokenService;

    public ApplicationTokenAuthenticationFilter(TokenService tokenService) {
        this.tokenService = tokenService;
    }
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("X-Auth-Token");

        if(tokenService.isValid(token))
            return true;

        logger.info("Request from '{}' with invalid token '{}' denied", request.getRemoteAddr(), token);
        response.setStatus(401);
        return false;
    }
}
