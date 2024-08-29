package com.example.chatting.service;

import com.example.chatting.domain.ChatMessage;
import com.example.chatting.repository.ChatMsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RabbitMqService {

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    @Value("${rabbitmq.queue.name}")
    private String queueName;

    private final RabbitTemplate rabbitTemplate;
    private final ChatMsRepository chatMsRepository;

    private final SimpMessageSendingOperations messageTemplate;

    public void sendMessage(ChatMessage messageDto) {

        //rabbitTemplate.convertAndSend(exchangeName, "chat.room." + messageDto.getGroupId(), messageDto);
        messageTemplate.convertAndSend("/sub/chat/room/"+messageDto.getGroupId(), messageDto);
    }

//    @RabbitListener(queues = "#{chatQueue.name}")
//    public void receiveMessage(ChatMessage messageDto) throws Exception {
//
//        // Null 체크
//        if (messageDto == null) {
//            throw new IllegalArgumentException("Message cannot be null");
//        }
//
//        Long roomId = messageDto.getGroupId();
//
//        // roomId가 null인지 확인
//        if (roomId == null) {
//            throw new IllegalArgumentException("Room ID cannot be null");
//        }
//
//        // Redis에 저장할 때 hash key가 null이 아니도록 확인
//        String roomIdStr = roomId.toString(); // Long을 String으로 변환
//        if (roomIdStr.isEmpty()) {
//            throw new IllegalArgumentException("Room ID cannot be empty");
//        }
//
//        chatMsRepository.save(messageDto);
//        //rabbitTemplate.convertAndSend("/exchange/chat.exchange/chat.room." + roomId, messageDto);
//        //rabbitTemplate.convertAndSend(exchangeName, "chat.room." + messageDto.getGroupId(), messageDto);
//    }

}
