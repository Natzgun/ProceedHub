package com.mistysoft.proceedhub.modules.user.application;

public interface TokenIssuer {
    IssuedToken issue(String username);

    record IssuedToken(String value, long maxAgeSeconds) {
    }
}
