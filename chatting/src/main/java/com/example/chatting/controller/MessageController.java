package com.example.chatting.controller;

import com.example.chatting.domain.ChatMessage;
import com.example.chatting.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MessageController {

    private final ChatRoomService chatRoomService;

    @MessageMapping("/sendMessage")
    public void sendMessage(@Payload ChatMessage messageDto) {

        chatRoomService.sendMessage(messageDto);

    }

}
