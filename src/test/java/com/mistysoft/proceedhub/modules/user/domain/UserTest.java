package com.mistysoft.proceedhub.modules.user.domain;

import org.junit.jupiter.api.Test;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    @Test
    void rejectsInvalidIdentityAndCannotBeGrantedAdminAtRegistration() {
        assertThrows(IllegalArgumentException.class, () -> new UserId(" "));
        assertThrows(IllegalArgumentException.class,
                () -> User.register(new UserId("id"), "alice", "invalid", "hash"));
        User user = User.register(new UserId("id"), "alice", "alice@example.com", "hash");
        assertEquals(Set.of(Role.USER), user.getRoles());
        assertThrows(UnsupportedOperationException.class, () -> user.getRoles().add(Role.ADMIN));
    }
}
