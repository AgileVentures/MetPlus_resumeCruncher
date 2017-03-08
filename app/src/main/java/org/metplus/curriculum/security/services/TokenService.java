package org.metplus.curriculum.security.services;

public interface TokenService {
    boolean isValid(String token);

    String generateToken(String ipAddress);

    int totalNumberTokens();
}
