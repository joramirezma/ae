package com.riwi.assesment.infrastructure.adapter.out.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.riwi.assesment.domain.model.User;
import com.riwi.assesment.domain.port.out.UserRepositoryPort;
import com.riwi.assesment.infrastructure.adapter.out.persistence.entity.UserEntity;
import com.riwi.assesment.infrastructure.adapter.out.persistence.mapper.UserMapper;
import com.riwi.assesment.infrastructure.adapter.out.persistence.repository.JpaUserRepository;

/**
 * Persistence adapter implementing UserRepositoryPort.
 * Adapts the domain port to JPA infrastructure.
 */
@Component
public class UserPersistenceAdapter implements UserRepositoryPort {

    private final JpaUserRepository jpaUserRepository;

    public UserPersistenceAdapter(JpaUserRepository jpaUserRepository) {
        this.jpaUserRepository = jpaUserRepository;
    }

    @Override
    public User save(User user) {
        UserEntity entity;
        
        // Check if it's a new entity (not in database) or existing
        if (user.getId() != null && jpaUserRepository.existsById(user.getId())) {
            // Update existing entity
            entity = UserMapper.toEntity(user);
        } else {
            // Create new entity without ID (let JPA generate it)
            entity = new UserEntity();
            entity.setUsername(user.getUsername());
            entity.setEmail(user.getEmail());
            entity.setPassword(user.getPassword());
        }
        
        UserEntity savedEntity = jpaUserRepository.save(entity);
        return UserMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return jpaUserRepository.findById(id)
                .map(UserMapper::toDomain);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return jpaUserRepository.findByUsername(username)
                .map(UserMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaUserRepository.findByEmail(email)
                .map(UserMapper::toDomain);
    }

    @Override
    public boolean existsByUsername(String username) {
        return jpaUserRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaUserRepository.existsByEmail(email);
    }
}
