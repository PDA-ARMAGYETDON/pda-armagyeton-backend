package com.example.chatting.controller;

import com.example.chatting.domain.ChatMessage;
import com.example.chatting.repository.ChatMsRepository;
import com.example.chatting.service.RabbitMqService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MessageController {

    private final SimpMessageSendingOperations messageTemplate;
    private final ChatMsRepository chatMsRepository;
    //@MessageMapping("chat.message.{groupId}")
//    public void sendMessage(final @RequestBody ChatMessage messageDto,@DestinationVariable Long groupId) {
//        messageDto.setGroupId(groupId);
//        rabbitMqService.sendMessage(messageDto);
//    }
    @MessageMapping("/sendMessage")
    public void sendMessage(@Payload ChatMessage messageDto) {
        //messageDto.setGroupId(groupId);
        //rabbitMqService.sendMessage(messageDto);
        messageTemplate.convertAndSend("/sub/chat/room/"+messageDto.getGroupId(), messageDto);
        chatMsRepository.save(messageDto);
    }

}
