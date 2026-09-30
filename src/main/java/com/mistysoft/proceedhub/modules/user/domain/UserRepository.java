package com.mistysoft.proceedhub.modules.user.domain;

import java.util.Optional;

public interface UserRepository {
    void save(User user);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
}
