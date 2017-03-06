package org.metplus.curriculum.useCases;

public class UserTokenAuthentication {
    public boolean canLogin(String token) {
        if(token.equals("123456"))
            return true;
        return false;
    }
}
