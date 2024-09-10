package com.example.alarm.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Component
public class FCMInitializer {

    @Value("${fcm.service.key}")
    private String googleApplicationCredentials;

    @PostConstruct
    public void initialize() {
        try (InputStream is = getClass().getResourceAsStream(googleApplicationCredentials)) {
            if (is == null) {
                throw new IOException("Resource not found: " + googleApplicationCredentials);
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(is))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                log.info("FirebaseApp 초기화 성공");
            }
        } catch (IOException e) {
            e.printStackTrace();
            log.error("FirebaseApp 초기화 실패");
        }
    }
}
