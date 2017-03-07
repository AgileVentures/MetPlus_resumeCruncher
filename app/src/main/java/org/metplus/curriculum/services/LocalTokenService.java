package org.metplus.curriculum.services;

import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class LocalTokenService implements TokenService {
    private static Logger logger = LoggerFactory.getLogger(LocalTokenService.class);

    private final int timeoutSeconds;
    private HashMap<UUID, TokenInformation> tokens = new HashMap<>();
    private ObjectIdGenerators.UUIDGenerator generator = new ObjectIdGenerators.UUIDGenerator();

    public LocalTokenService() {
        this.timeoutSeconds = 1800;
    }

    public LocalTokenService(int timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
    }

    @Override
    public synchronized boolean isValid(String token) {
        logger.trace("Checking is token: '{}' is valid", token);
        UUID tokenUUID = UUID.fromString(token);
        if (tokens.containsKey(tokenUUID)) {
            if (new Date().getTime() <
                    (tokens.get(tokenUUID).getEntryDate().getTime() + timeoutSeconds))
                return true;
            else
                logger.info("Token '{}' for ip '{}' expired", token, tokens.get(tokenUUID).getIpAddress());
        } else
            logger.info("Token '{}' is invalid", token);
        return false;
    }

    @Override
    public synchronized String generateToken(String ipAddress) {
        logger.trace("generateToken({})", ipAddress);

        for (UUID token : tokens.keySet())
            if (tokens.get(token).getIpAddress().compareTo(ipAddress) == 0)
                if (isValid(token.toString())) {
                    logger.debug("Token for ip '{}' is still valid, no need to regenerate", ipAddress);
                    return token.toString();
                } else {
                    logger.debug("Token for ip '{}' is no longer valid, need to generate a new one", ipAddress);
                    tokens.remove(token);
                    break;
                }
        UUID token = generator.generateId(ipAddress);
        logger.info("Generated token '{}' valid for '{}' seconds", token.toString(), timeoutSeconds);
        tokens.put(token, new TokenInformation(ipAddress, new Date()));
        return token.toString();
    }

    @Override
    public synchronized int totalNumberTokens() {
        return tokens.size();
    }

    class TokenInformation {
        private String ipAddress;
        private Date entryDate;

        TokenInformation(String ipAddress, Date entryDate) {
            this.ipAddress = ipAddress;
            this.entryDate = entryDate;
        }

        Date getEntryDate() {
            return entryDate;
        }

        String getIpAddress() {
            return ipAddress;
        }
    }
}
