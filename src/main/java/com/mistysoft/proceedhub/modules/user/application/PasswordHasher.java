package com.mistysoft.proceedhub.modules.user.application;

public interface PasswordHasher {
    String hash(String password);
    boolean matches(String password, String hash);
}
