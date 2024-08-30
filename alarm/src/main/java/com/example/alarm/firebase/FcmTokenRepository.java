package com.example.alarm.firebase;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FcmTokenRepository extends JpaRepository<FcmToken, Integer> {
    Optional<FcmToken> findByUserId(String userId);
}
