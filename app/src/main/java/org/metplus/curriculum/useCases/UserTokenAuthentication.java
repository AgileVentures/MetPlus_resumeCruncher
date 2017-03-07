package org.metplus.curriculum.useCases;

import org.metplus.curriculum.services.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class UserTokenAuthentication {
    private static Logger logger = LoggerFactory.getLogger(UserTokenAuthentication.class);

    @Autowired
    private TokenService tokenService;
    public UserTokenAuthentication(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    public boolean canLogin(String token) {
        logger.trace("canLogin(" + token + ")");

        if(tokenService.isValid(token)) {
            logger.debug("Login successfull with token: " + token);
            return true;
        }
        logger.debug("Token '" + token + "' is not valid");
        return false;
    }
}
