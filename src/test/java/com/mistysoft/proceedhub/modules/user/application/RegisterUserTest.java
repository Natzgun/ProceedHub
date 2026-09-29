package com.mistysoft.proceedhub.modules.user.application;

import com.mistysoft.proceedhub.modules.user.domain.*;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RegisterUserTest {
    private final UserRepository repository = mock(UserRepository.class);
    private final PasswordHasher hasher = mock(PasswordHasher.class);
    private final RegisterUser register = new RegisterUser(repository, hasher);

    @Test
    void registersOnlyUserRoleWithHashedPassword() {
        when(hasher.hash("secret")).thenReturn("hashed");
        User user = register.execute("alice", "alice@example.com", "secret");
        assertEquals("hashed", user.getPasswordHash());
        assertEquals(java.util.Set.of(Role.USER), user.getRoles());
        verify(repository).save(user);
    }

    @Test
    void rejectsDuplicateUsernameOrEmail() {
        User existing = User.register(new UserId("id"), "alice", "alice@example.com", "hash");
        when(repository.findByUsername("alice")).thenReturn(Optional.of(existing));
        assertThrows(IllegalArgumentException.class, () -> register.execute("alice", "other@example.com", "secret"));
        when(repository.findByEmail("alice@example.com")).thenReturn(Optional.of(existing));
        assertThrows(IllegalArgumentException.class, () -> register.execute("other", "alice@example.com", "secret"));
        verify(repository, never()).save(any());
    }

    @Test
    void rejectsEmptyInputBeforeHashing() {
        assertThrows(IllegalArgumentException.class, () -> register.execute("alice", "a@b.com", " "));
        verifyNoInteractions(repository, hasher);
    }
}
