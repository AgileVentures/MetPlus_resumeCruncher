package org.metplus.curriculum.security;

import com.google.common.base.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;

public class BackendAdminUsernamePasswordAuthenticationProvider implements AuthenticationProvider {

    private final static Logger logger = LoggerFactory.getLogger(BackendAdminUsernamePasswordAuthenticationProvider.class);
    public static final String INVALID_BACKEND_ADMIN_CREDENTIALS = "Invalid Backend Admin Credentials";

    @Value("${backend.admin.username}")
    private String backendAdminUsername;

    @Value("${backend.admin.password}")
    private String backendAdminPassword;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Optional<String> username = (Optional) authentication.getPrincipal();
        Optional<String> password = (Optional) authentication.getCredentials();
        logger.info("Authenticate({}, {})", username, password);

        if (credentialsMissing(username, password) || credentialsInvalid(username, password)) {
            throw new BadCredentialsException(INVALID_BACKEND_ADMIN_CREDENTIALS);
        }

        return new UsernamePasswordAuthenticationToken(username.get(), null,
                AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_BACKEND_ADMIN"));
    }

    private boolean credentialsMissing(Optional<String> username, Optional<String> password) {
        return !username.isPresent() || !password.isPresent();
    }

    private boolean credentialsInvalid(Optional<String> username, Optional<String> password) {
        logger.info("Authenticate({}, {})", username, password);
        logger.info("is user admin? {}", !isBackendAdmin(username.get()));
        logger.info("is password admin? {}", !password.get().equals(backendAdminPassword));
        return !isBackendAdmin(username.get()) || !password.get().equals(backendAdminPassword);
    }

    private boolean isBackendAdmin(String username) {
        logger.info("isBackendAdmin({})", username);
        logger.info("is admin? {}", backendAdminUsername.equals(username));
        return backendAdminUsername.equals(username);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(BackendAdminUsernamePasswordAuthenticationToken.class);
    }
}
