package org.metplus.curriculum.web;

import org.metplus.curriculum.security.AuthenticationWithToken;
import org.metplus.curriculum.security.ExternalServiceAuthenticator;
import org.metplus.curriculum.web.controllers.User;
import org.springframework.security.core.authority.AuthorityUtils;

/**
 * Created by joaopereira on 2/14/2016.
 */
public class BasicUserAuthenticator implements ExternalServiceAuthenticator {
    @Override
    public AuthenticationWithToken authenticate(String username, String password) {
        BasicExternalTokenAuthentication authenticatedExternalWebService = new BasicExternalTokenAuthentication(new User(username), null,
                AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_DOMAIN_USER"));

        return authenticatedExternalWebService;
    }
}
