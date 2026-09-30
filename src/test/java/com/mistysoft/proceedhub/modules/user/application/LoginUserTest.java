package com.mistysoft.proceedhub.modules.user.application;

import com.mistysoft.proceedhub.modules.user.domain.*;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoginUserTest {
    private final UserRepository repository = mock(UserRepository.class);
    private final PasswordHasher hasher = mock(PasswordHasher.class);
    private final LoginUser login = new LoginUser(repository, hasher);

    @Test
    void returnsThePersistedUserOnValidCredentials() {
        User user = User.register(new UserId("id"), "alice", "alice@example.com", "hash");
        when(repository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(hasher.matches("secret", "hash")).thenReturn(true);
        assertSame(user, login.execute("alice", "secret"));
    }

    @Test
    void usesSameErrorForUnknownUserAndIncorrectPassword() {
        String unknown = assertThrows(IllegalArgumentException.class,
                () -> login.execute("nobody", "secret")).getMessage();
        User user = User.register(new UserId("id"), "alice", "alice@example.com", "hash");
        when(repository.findByUsername("alice")).thenReturn(Optional.of(user));
        String incorrect = assertThrows(IllegalArgumentException.class,
                () -> login.execute("alice", "wrong")).getMessage();
        assertEquals(unknown, incorrect);
    }
}
