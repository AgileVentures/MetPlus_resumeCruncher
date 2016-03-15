package org.metplus.curriculum.security.service;

import org.metplus.curriculum.security.AuthenticationWithToken;
import org.metplus.curriculum.security.ExternalServiceAuthenticator;
import org.metplus.curriculum.web.controllers.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.AuthorityUtils;

/**
 * Created by joaopereira on 2/14/2016.
 */
public class BasicUserAuthenticator implements ExternalServiceAuthenticator {
    private final static Logger logger = LoggerFactory.getLogger(BasicUserAuthenticator.class);

    public static final String INVALID_BACKEND_ADMIN_CREDENTIALS = "Invalid Backend Admin Credentials";

    @Value("${backend.admin.username}")
    private String backendAdminUsername;

    @Value("${backend.admin.password}")
    private String backendAdminPassword;
    @Override
    public AuthenticationWithToken authenticate(String username, String password) {
        logger.error("authenticate({}, {})", username, password);
        System.out.println("authenticate(" + username + ", " + password+ ")");
        if(credentialsInvalid(username, password)) {
            throw new BadCredentialsException(INVALID_BACKEND_ADMIN_CREDENTIALS);
        }
        BasicExternalTokenAuthentication authenticatedExternalWebService = new BasicExternalTokenAuthentication(new User(username), null,
                AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_DOMAIN_USER"));

        return authenticatedExternalWebService;
    }
    private boolean credentialsInvalid(String username, String password) {
        return !isBackendAdmin(username) || !password.equals(backendAdminPassword);
    }

    private boolean isBackendAdmin(String username) {
        return backendAdminUsername.equals(username);
    }
}
