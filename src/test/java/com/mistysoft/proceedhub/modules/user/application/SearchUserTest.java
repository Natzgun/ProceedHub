package com.mistysoft.proceedhub.modules.user.application;

import com.mistysoft.proceedhub.modules.user.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class SearchUserTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SearchUser searchUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSearchUserFound() {
        String username = "testuser";
        User user = User.register(new UserId(UUID.randomUUID().toString()),
                username, "test@example.com", "hash");
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        Optional<User> result = searchUser.findByUsername(username);

        assertTrue(result.isPresent());
        assertEquals(user, result.get());
    }

    @Test
    void testSearchUserNotFound() {
        String username = "testuser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        Optional<User> result = searchUser.findByUsername(username);

        assertTrue(result.isEmpty());
    }
}
