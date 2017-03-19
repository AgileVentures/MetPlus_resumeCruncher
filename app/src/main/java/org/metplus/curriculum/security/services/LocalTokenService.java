package org.metplus.curriculum.security.services;

import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Clock;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class LocalTokenService implements TokenService {
    private static Logger logger = LoggerFactory.getLogger(LocalTokenService.class);

    private final int timeoutSeconds;
    private HashMap<UUID, TokenInformation> tokens = new HashMap<>();
    private ObjectIdGenerators.UUIDGenerator generator = new ObjectIdGenerators.UUIDGenerator();

    private Clock clock;

    public LocalTokenService() {
        this.timeoutSeconds = 1800;
        clock = Clock.systemDefaultZone();
    }

    public LocalTokenService(int timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
    }

    public LocalTokenService(Clock clock, int timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
        this.clock = clock;
    }

    @Override
    public synchronized boolean isValid(String token) {
        logger.trace("Checking is token: '{}' is valid", token);

        if(token == null)
            return false;

        UUID tokenUUID = null;
        try {
            tokenUUID = UUID.fromString(token);
        } catch (IllegalArgumentException exp) {
            logger.warn("Token '{}' is not a UUID", token);
            return false;
        }

        if (tokens.containsKey(tokenUUID)) {
            if (clock.instant().compareTo(tokens.get(tokenUUID).getEntryDate().plusSeconds(timeoutSeconds)) < 0)
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
        tokens.put(token, new TokenInformation(ipAddress, clock.instant()));
        return token.toString();
    }

    @Override
    public synchronized int totalNumberTokens() {
        return tokens.size();
    }

    class TokenInformation {
        private String ipAddress;
        private Instant entryDate;

        TokenInformation(String ipAddress, Instant entryDate) {
            this.ipAddress = ipAddress;
            this.entryDate = entryDate;
        }

        Instant getEntryDate() {
            return entryDate;
        }

        String getIpAddress() {
            return ipAddress;
        }
    }
}
