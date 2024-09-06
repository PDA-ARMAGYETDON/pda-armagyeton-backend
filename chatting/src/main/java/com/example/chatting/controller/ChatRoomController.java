package com.example.chatting.controller;


import com.example.chatting.domain.ChatMessage;
import com.example.chatting.domain.ChatRoom;
import com.example.chatting.dto.ChatMessageResponse;
import com.example.chatting.service.ChatRoomService;
import com.example.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "팀 채팅방을 생성하는 API")
    @PostMapping("/rooms")
    public ApiResponse create(@RequestAttribute("teamId") int teamId) {
        chatRoomService.createChatRoom(teamId);
        return new ApiResponse<>(201, true, "채팅방이 생성되었습니다.", null);
    }

    @Operation(summary = "팀의 채팅내역 조회하는 API")
    @GetMapping("/rooms")
    public ApiResponse<List<ChatMessageResponse>> getMessages(@RequestAttribute("teamId") int teamId) {

        List<ChatMessageResponse> messageDtos = chatRoomService.selectChatMessageList(teamId);

        return new ApiResponse<>(200, true,"팀의 채팅방 내역을 조회했습니다.", messageDtos);

    }

}