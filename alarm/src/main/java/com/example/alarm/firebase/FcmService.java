package com.example.alarm.firebase;


import com.example.alarm.firebase.dto.FcmTokenResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmService {

    private final FcmTokenRepository fcmTokenRepository;

    @RabbitListener(queues = "${spring.rabbitmq.reg-token-queue.name}")
    public void registerTokenMessage(String message) {
        try {

            FcmTokenResponseDto fcmTokenResponseDto = new ObjectMapper().readValue(message, FcmTokenResponseDto.class);
            log.info(" [Alarm] 받은 정보: '{}'", fcmTokenResponseDto.toString());

            if (!fcmTokenRepository.findByUserId(fcmTokenResponseDto.getUserId()).isPresent()) {
                fcmTokenRepository.save(new FcmToken(fcmTokenResponseDto.getUserId(), fcmTokenResponseDto.getFcmToken()));
            }

            subscribeToTopics(fcmTokenResponseDto.getFcmToken(), fcmTokenResponseDto.getTeamIds());

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            log.error("Json -> Object convert Error!!");
        }

    }

    private void sendNotificationToTopic(String topic, String title, String body) {
        Message fcmMessage = Message.builder()
                .setTopic(topic)
                .setNotification(Notification.builder().setTitle(title).setBody(body).build())
                .build();

        FirebaseMessaging.getInstance().sendAsync(fcmMessage);
    }

    //토픽 구독
    public void subscribeToTopics(String fcmToken, ArrayList<Integer> groupIds) {
        for (Integer groupId : groupIds) {
            String topic = "group_" + groupId; // 그룹 번호를 토픽 이름으로 설정
            try {
                FirebaseMessaging.getInstance().subscribeToTopic(Arrays.asList(fcmToken), topic);
                log.info("[FCM] : 그룹 {} 구독 성공 ", topic);
            } catch (FirebaseMessagingException e) {
                e.printStackTrace();
                log.info("[FCM] : 그룹 {} 구독 실패 ", topic);
            }
        }
    }

}
