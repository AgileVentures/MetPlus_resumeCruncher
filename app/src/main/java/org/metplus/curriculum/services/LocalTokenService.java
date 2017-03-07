package org.metplus.curriculum.services;

import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class LocalTokenService implements TokenService {

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
        UUID tokenUUID = UUID.fromString(token);
        if (tokens.containsKey(tokenUUID))
            if (new Date().getTime() <
                    (tokens.get(tokenUUID).getEntryDate().getTime() + timeoutSeconds))
                return true;
        return false;
    }

    @Override
    public synchronized String generateToken(String ipAddress) {
        for (UUID token : tokens.keySet())
            if (tokens.get(token).getIpAddress().compareTo(ipAddress) == 0)
                if (isValid(token.toString())) {
                    return token.toString();
                } else {
                    tokens.remove(token);
                    break;
                }
        UUID token = generator.generateId(ipAddress);
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
