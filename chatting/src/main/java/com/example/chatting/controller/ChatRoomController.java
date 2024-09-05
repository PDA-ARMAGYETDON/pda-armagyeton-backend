package com.example.chatting.controller;


import com.example.chatting.domain.ChatMessage;
import com.example.chatting.domain.ChatRoom;
import com.example.chatting.dto.ChatMessageResponse;
import com.example.chatting.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @PostMapping("/room")
    public ResponseEntity<String> create(@RequestBody ChatRoom chatroom) {
        try {
            chatRoomService.createChatRoom(chatroom.getTeamId());
            return ResponseEntity.status(HttpStatus.CREATED).body("채팅방 생성되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating chat room");
        }
    }

    @GetMapping("/room")
    public ResponseEntity<List<ChatMessageResponse>> getMessages(@RequestParam("teamId") int teamId) {
        try {
            log.info("채팅 메시지 조회 요청 시작 - 팀 ID: {}", teamId);
            List<ChatMessage> messages = chatRoomService.selectChatMessageList(teamId);

            if (messages.isEmpty()) {
                log.warn("팀 ID {}에 대한 메시지가 없습니다.", teamId);
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }

            log.info("채팅 메시지 조회 완료 - 메시지 수: {}", messages.size());
            List<ChatMessageResponse> messageDtos = messages.stream()
                    .map(message -> new ChatMessageResponse(message.getUserId(), message.getMessage()))
                    .toList();
            return ResponseEntity.ok(messageDtos);

        } catch (Exception e) {
            log.error("채팅 메시지 조회 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}
