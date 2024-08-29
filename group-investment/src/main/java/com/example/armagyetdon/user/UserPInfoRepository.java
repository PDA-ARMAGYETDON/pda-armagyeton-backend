package com.example.armagyetdon.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserPInfoRepository extends JpaRepository<UserPInfo, Integer> {
    Optional<UserPInfo> findById(int userId);
}
