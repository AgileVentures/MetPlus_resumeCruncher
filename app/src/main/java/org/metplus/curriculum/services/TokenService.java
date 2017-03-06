package org.metplus.curriculum.services;

public interface TokenService {
    boolean isValid(String token);

    String generateToken(String ipAddress);
}
