package org.metplus.curriculum.services;

import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class LocalTokenService implements TokenService {

    class TokenInformation {
        private String ipAddress;
        private Date entryDate;

        public TokenInformation(String ipAddress, Date entryDate) {
            this.ipAddress = ipAddress;
            this.entryDate = entryDate;
        }

        public Date getEntryDate() {
            return entryDate;
        }

        public String getIpAddress() {
            return ipAddress;
        }
    }

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
    public boolean isValid(String token) {

        if (tokens.containsKey(UUID.fromString(token)))
            return true;
        return false;
    }

    @Override
    public String generateToken(String ipAddress) {
        if(tokens.containsValue(ipAddress))
            for(UUID token : tokens.keySet())
                if(tokens.get(token).equals(ipAddress))
                    return token.toString();
        UUID token = generator.generateId(ipAddress);
        tokens.put(token, new TokenInformation(ipAddress, new Date()));
        return token.toString();
    }
}
