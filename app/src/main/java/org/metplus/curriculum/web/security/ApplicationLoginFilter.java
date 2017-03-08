package org.metplus.curriculum.web.security;

import org.metplus.curriculum.services.TokenService;
import org.metplus.curriculum.useCases.UserTryToLogin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

public class ApplicationLoginFilter extends HandlerInterceptorAdapter {
    private static Logger logger = LoggerFactory.getLogger(ApplicationLoginFilter.class);

    @Autowired
    private UserTryToLogin loginUseCase;

    @Autowired
    private TokenService tokenService;

    public ApplicationLoginFilter(UserTryToLogin loginUseCase, TokenService tokenService) {
        this.loginUseCase = loginUseCase;
        this.tokenService = tokenService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String username = request.getHeader("X-Auth-Username");
        String password = request.getHeader("X-Auth-Password");

        if(loginUseCase.canUserLogin(username, password)) {
            logger.info("Login successful for '{}'", username);
            response.setStatus(200);
            response.getOutputStream().print("{\"token\": \"" + tokenService.generateToken(request.getRemoteAddr()) + "\"}");

            response.getOutputStream().flush();
            response.setContentType("application/json");
        } else {
            logger.info("Invalid credentials on request from '{}'", request.getRemoteAddr());
            response.setStatus(401);
        }
        return false;
    }
}
