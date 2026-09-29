package com.mistysoft.proceedhub.modules.user.infrastructure;

import com.mistysoft.proceedhub.modules.user.domain.*;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JpaUserRepositoryTest {
    private final ISpringDataUserRepository springData = mock(ISpringDataUserRepository.class);
    private final JpaUserRepository repository = new JpaUserRepository(springData);

    @Test
    void persistsAndRestoresUserWithRoleAndPasswordHash() {
        User user = User.restore(new UserId("id"), "admin", "a@example.com", "hash", Set.of(Role.ADMIN));
        repository.save(user);
        var captor = org.mockito.ArgumentCaptor.forClass(UserEntity.class);
        verify(springData).save(captor.capture());
        UserEntity entity = captor.getValue();
        assertEquals("hash", entity.getPassword());
        when(springData.findByEmail("a@example.com")).thenReturn(Optional.of(entity));
        User restored = repository.findByEmail("a@example.com").orElseThrow();
        assertEquals(user.getId(), restored.getId());
        assertEquals(Set.of(Role.ADMIN), restored.getRoles());
    }
}
