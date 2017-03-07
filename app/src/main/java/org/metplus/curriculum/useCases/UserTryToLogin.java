package org.metplus.curriculum.useCases;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

public class UserTryToLogin {
    private static Logger logger = LoggerFactory.getLogger(UserTryToLogin.class);

    @Value("${backend.admin.password}")
    private String password;

    @Value("${backend.admin.username}")
    private String username;

    public UserTryToLogin(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public boolean canUserLogin(String username, String password) {
        logger.trace("canUserLogin({}, {})", username, password);
        if(username.equals(this.username) && password.equals(this.password)) {
            logger.info("User '{}' login was successfull", username);
            return true;
        }
        logger.info("Unable to login user '{}'", username);
        return false;
    }
}
