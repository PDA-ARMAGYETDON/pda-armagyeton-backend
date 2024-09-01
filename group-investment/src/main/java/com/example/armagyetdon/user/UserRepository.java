package com.example.armagyetdon.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Boolean existsByLoginId(String loginId);
    Optional<User> findByLoginId(String loginId);
}
