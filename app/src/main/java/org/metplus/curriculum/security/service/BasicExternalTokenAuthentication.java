package org.metplus.curriculum.security.service;

import org.metplus.curriculum.security.AuthenticationWithToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class BasicExternalTokenAuthentication extends AuthenticationWithToken {


    public BasicExternalTokenAuthentication(Object aPrincipal, Object aCredentials, Collection<? extends GrantedAuthority> anAuthorities) {
        super(aPrincipal, aCredentials, anAuthorities);
    }

}