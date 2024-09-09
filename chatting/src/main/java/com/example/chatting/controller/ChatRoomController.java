package com.example.chatting.controller;


import com.example.chatting.dto.ChatMessageResponse;
import com.example.chatting.service.ChatRoomService;
import com.example.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @Operation(summary = "헬스체크 API", description = "서버가 정상적으로 동작하는지 확인하는 API")
    @GetMapping("/health-check")
    public ApiResponse healthCheck() {
        return new ApiResponse<>(200, true, "서버가 정상적으로 동작합니다.", null);
    }

    @Operation(summary = "팀 채팅방을 생성하는 API")
    @PostMapping("/rooms/{id}")
    public ApiResponse create(@PathVariable("id") int teamId) {
        chatRoomService.createChatRoom(teamId);
        return new ApiResponse<>(201, true, "채팅방이 생성되었습니다.", null);
    }

    @Operation(summary = "팀의 채팅내역 조회하는 API")
    @GetMapping("/rooms/{id}")
    public ApiResponse<List<ChatMessageResponse>> getMessages(@PathVariable("id") int teamId) {

        List<ChatMessageResponse> messageDtos = chatRoomService.selectChatMessageList(teamId);

        return new ApiResponse<>(200, true, "팀의 채팅방 내역을 조회했습니다.", messageDtos);

    }

    @Operation(summary = "팀 멤버 이름 조회 API")
    @GetMapping("/rooms/{id}/members")
    public ApiResponse<List<String>> getTeamMemberNames(@PathVariable("id") int teamId) {
        List<String> memberNames = chatRoomService.getTeamMemberNames(teamId);
        return new ApiResponse<>(200, true, "팀 멤버 이름 리스트를 조회했습니다.", memberNames);
    }

}