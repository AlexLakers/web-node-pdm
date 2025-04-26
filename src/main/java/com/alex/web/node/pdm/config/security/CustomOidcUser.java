package com.alex.web.node.pdm.config.security;



import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.Map;

/**
 * This class contains all the necessary fields for describing authenticated user.
 * This class also implements two interfaces for different providers(Oidc and Dao).
 *
 * @see OidcUser oidcUser
 * @see UserDetails userDetail
 */

public class CustomOidcUser extends CustomUserDetails implements OidcUser {

    private final OidcUser oidcUser;

    public CustomOidcUser(OidcUser oidcUser, Long id) {
        super(oidcUser.getClaim("email"), null, oidcUser.getAuthorities(), id);
        this.oidcUser=oidcUser;
    }


    @Override
    public String getName() {
        return oidcUser.getName();
    }

    @Override
    public Map<String, Object> getClaims() {
        return oidcUser.getClaims();
    }

    @Override
    public OidcUserInfo getUserInfo() {
        return oidcUser.getUserInfo();
    }

    @Override
    public OidcIdToken getIdToken() {
        return oidcUser.getIdToken();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return oidcUser.getAttributes();
    }
}
