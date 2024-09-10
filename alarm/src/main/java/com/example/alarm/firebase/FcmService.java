package com.example.alarm.firebase;


import com.example.alarm.firebase.dto.*;
import com.example.alarm.firebase.exception.AlarmErrorCode;
import com.example.alarm.firebase.exception.AlarmException;
import com.example.common.exception.ErrorCode;
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
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmService {

    private final FcmTokenRepository fcmTokenRepository;

    @RabbitListener(queues = "${spring.rabbitmq.reg-token-queue.name}")
    public void registerTokenMessage(String message) {
        try {

            FcmTokenResponseDto fcmTokenResponseDto = new ObjectMapper().readValue(message, FcmTokenResponseDto.class);

            if (!fcmTokenRepository.findByUserId(fcmTokenResponseDto.getUserId()).isPresent()) {
                fcmTokenRepository.save(new FcmToken(fcmTokenResponseDto.getUserId(), fcmTokenResponseDto.getFcmToken()));
            }

            subscribeToTopics(fcmTokenResponseDto.getFcmToken(), fcmTokenResponseDto.getTeamIds());

        } catch (JsonProcessingException e) {
            throw new AlarmException(ErrorCode.JSON_PARSE_ERROR);
        }

    }

    @RabbitListener(queues = "${spring.rabbitmq.delete-fcm-queue.name}")
    public void deleteTokenMessage(String message) {
        try {

            FcmTokenDeleteDto fcmTokenDeleteDto = new ObjectMapper().readValue(message, FcmTokenDeleteDto.class);

            String foundedToken = fcmTokenRepository.findByUserId(fcmTokenDeleteDto.getUserId())
                    .orElseThrow(() -> new AlarmException(AlarmErrorCode.FCM_TOKEN_NOT_FOUND)).getFcmToken();

            unSubscribeToTopics(foundedToken, fcmTokenDeleteDto.getTeamList());

            fcmTokenRepository.deleteByUserId(fcmTokenDeleteDto.getUserId());


        } catch (JsonProcessingException e) {
            throw new AlarmException(ErrorCode.JSON_PARSE_ERROR);
        }

    }

    @RabbitListener(queues = "${spring.rabbitmq.stockTradeQueue.name}")
    private void stockTradeAlarmToTopic(String message) {

        try {
            StockAlarmDto alarmDto = new ObjectMapper().readValue(message, StockAlarmDto.class);
            StringBuilder sb = new StringBuilder(alarmDto.getStockName() + " " + alarmDto.getQuantity() + "주를 ");

            if (alarmDto.isBuy()) {
                sb.append("매수하였습니다.");
            } else {
                sb.append("매도하였습니다.");
            }

            String topic = String.valueOf(alarmDto.getTeamId());
            String title = "[" + alarmDto.getTeamName() + "]";
            String body = sb.toString();

            Message fcmMessage = Message.builder()
                    .setTopic(topic)
                    .setNotification(Notification.builder().setTitle(title).setBody(body).build())
                    .build();

            FirebaseMessaging.getInstance().sendAsync(fcmMessage);
        } catch (JsonProcessingException e) {
            throw new AlarmException(ErrorCode.JSON_PARSE_ERROR);
        }
    }

    @RabbitListener(queues = "${spring.rabbitmq.chatQueue.name}")
    private void chatAlarmToTopic(String message) {

        try {

            ChatAlarmDto alarmDto = new ObjectMapper().readValue(message, ChatAlarmDto.class);

            String topic = String.valueOf(alarmDto.getTeamId());
            String title = alarmDto.getName();
            String body = alarmDto.getMessage();

            Message fcmMessage = Message.builder()
                    .setTopic(topic)
                    .setNotification(Notification.builder().setTitle(title).setBody(body).build())
                    .build();

            FirebaseMessaging.getInstance().sendAsync(fcmMessage);

            log.info("[FCM] : {}번 방 채팅 알림 전송 완료", topic);

        } catch (JsonProcessingException e) {
            throw new AlarmException(ErrorCode.JSON_PARSE_ERROR);
        }
    }


    @RabbitListener(queues = "${spring.rabbitmq.vote-alarm-queue.name}")
    private void voteStockAlarmToTopic(String message) {
        log.info("[FCM] : 투표 알림 전송 시작");

        try {

            VoteToAlarmDto data = new ObjectMapper().readValue(message, VoteToAlarmDto.class);

            String topic = String.valueOf(data.getTeamId());
            String title = "주식 매매 제안 알림";
            String body = data.getTeamName() + " 모임에서 새로운 주식 매매 제안이 올라왔습니다.";

            Message fcmMessage = Message.builder()
                    .setTopic(topic)
                    .setNotification(Notification.builder().setTitle(title).setBody(body).build())
                    .build();

            FirebaseMessaging.getInstance().sendAsync(fcmMessage);

            log.info("[FCM] : {}번 방 매매제안 알림 전송 완료", topic);

        } catch (JsonProcessingException e) {
            throw new AlarmException(ErrorCode.JSON_PARSE_ERROR);
        }
    }


    @RabbitListener(queues = "${spring.rabbitmq.rule-alarm-queue.name}")
    private void voteRuleAlarmToTopic(String message) {
        log.info("[FCM] : 투표 알림 전송 시작");
        try {

            VoteToAlarmDto data = new ObjectMapper().readValue(message, VoteToAlarmDto.class);

            String topic = String.valueOf(data.getTeamId());
            String title = "규칙 매매 제안 알림";
            String body = data.getTeamName() + " 모임에서 새로운 규칙 제안이 올라왔습니다.";

            Message fcmMessage = Message.builder()
                    .setTopic(topic)
                    .setNotification(Notification.builder().setTitle(title).setBody(body).build())
                    .build();

            FirebaseMessaging.getInstance().sendAsync(fcmMessage).get();
            

            log.info("[FCM] : {}번 방 규칙제안 알림 전송 완료", topic);

        } catch (JsonProcessingException e) {
            throw new AlarmException(ErrorCode.JSON_PARSE_ERROR);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    //토픽 구독
    public void subscribeToTopics(String fcmToken, ArrayList<Integer> groupIds) {
        for (Integer groupId : groupIds) {
            String topic = String.valueOf(groupId);
            try {
                FirebaseMessaging.getInstance().subscribeToTopic(Arrays.asList(fcmToken), topic);
                log.info("[FCM] : 그룹 {} 구독 성공 ", topic);
            } catch (FirebaseMessagingException e) {
                log.info("[FCM] : 그룹 {} 구독 실패 ", topic);
                throw new AlarmException(ErrorCode.JSON_PARSE_ERROR);
            }
        }
    }

    public void unSubscribeToTopics(String fcmToken, ArrayList<Integer> groupIds) {
        for (Integer groupId : groupIds) {
            String topic = String.valueOf(groupId);
            try {
                FirebaseMessaging.getInstance().unsubscribeFromTopic(Arrays.asList(fcmToken), topic);
                log.info("[FCM] : 그룹 {} 구독 성공 ", topic);
            } catch (FirebaseMessagingException e) {
                log.info("[FCM] : 그룹 {} 구독 실패 ", topic);
                throw new AlarmException(ErrorCode.JSON_PARSE_ERROR);
            }
        }
    }

}
