package org.metplus.curriculum.useCases;

import org.springframework.beans.factory.annotation.Value;

public class UserTryToLogin {
    @Value("${backend.admin.password}")
    private String password;

    @Value("${backend.admin.username}")
    private String username;

    public UserTryToLogin(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public boolean canUserLogin(String username, String password) {
        if(username.equals(this.username) && password.equals(this.password))
            return true;
        return false;
    }
}
