package com.example.alarm.firebase;


import com.example.alarm.firebase.dto.FcmTokenRequestDto;
import com.example.alarm.firebase.dto.MqChatDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmService {

    private final FcmTokenRepository fcmTokenRepository;

    public void saveFcmToken(FcmTokenRequestDto fcmTokenRequestDto) {
        fcmTokenRepository.save(new FcmToken(fcmTokenRequestDto.getUserId(), fcmTokenRequestDto.getFcmToken()));
    }

    @RabbitListener(queues = "${spring.rabbitmq.recvChatQueue.name}")
    public void receiveChatMessage(String message) {
        try {
            MqChatDto mqChatDto = new ObjectMapper().readValue(message, MqChatDto.class);
            System.out.println(" [Chat] 받은 정보: '" + mqChatDto.toString() + "'");

            sendNotificationToTopic(mqChatDto.getTeamId(), mqChatDto.getUserId(), mqChatDto.getChatMessage());

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

//    @Transactional
//    public void deleteFcmToken(int userId) {
//        fcmTokenRepository.deleteByUserId(userId);
//    }


}
