package com.mistysoft.proceedhub.modules.user.infrastructure;

import com.mistysoft.proceedhub.modules.user.domain.*;

public class UserMapper {

    private UserMapper() {
        throw new UnsupportedOperationException();
    }

    public static UserEntity toEntity(User user) {
        UserEntity entity = new UserEntity();
        entity.setId(user.getId().value());
        entity.setUsername(user.getUsername());
        entity.setEmail(user.getEmail());
        entity.setPassword(user.getPasswordHash());
        entity.setRoles(user.getRoles());
        return entity;
    }

    public static User toDomain(UserEntity entity) {
        return User.restore(new UserId(entity.getId()), entity.getUsername(), entity.getEmail(),
                entity.getPassword(), entity.getRoles());
    }
}
