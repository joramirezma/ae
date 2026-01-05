package com.riwi.assesment.infrastructure.adapter.out.persistence.mapper;

import com.riwi.assesment.domain.model.User;
import com.riwi.assesment.infrastructure.adapter.out.persistence.entity.UserEntity;

/**
 * Mapper to convert between User domain model and UserEntity JPA entity.
 */
public class UserMapper {

    private UserMapper() {
        // Utility class
    }

    public static User toDomain(UserEntity entity) {
        if (entity == null) {
            return null;
        }
        return User.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .email(entity.getEmail())
                .password(entity.getPassword())
                .build();
    }

    public static UserEntity toEntity(User domain) {
        if (domain == null) {
            return null;
        }
        return new UserEntity(
                domain.getId(),
                domain.getUsername(),
                domain.getEmail(),
                domain.getPassword()
        );
    }
}
