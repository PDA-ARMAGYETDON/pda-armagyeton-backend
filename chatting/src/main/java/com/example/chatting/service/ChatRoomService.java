package com.example.chatting.service;

import com.example.chatting.domain.ChatMessage;
import com.example.chatting.domain.ChatRoom;
import com.example.chatting.dto.ChatMessageResponse;
import com.example.chatting.exception.ChatErrorCode;
import com.example.chatting.exception.ChatException;
import com.example.chatting.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final SimpMessageSendingOperations messageTemplate;
    private final RedisTemplate<String, ChatMessage> redisTemplateForMessage;

    @Value("${redis.chatroom.prefix}")
    private String prefix;

    @Transactional
    public void createChatRoom(int teamId) {
        log.info("createChatRoom() 호출됨 - 팀 ID: {}", teamId);
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setTeamId(teamId);
        chatRoomRepository.save(chatRoom);
        log.info("채팅방이 성공적으로 생성됨 - 팀 ID: {}", teamId);
    }

    @Cacheable(value = "chatMessages", key = "#teamId")
    public List<ChatMessageResponse> selectChatMessageList(int teamId) {

        chatRoomRepository.findById(teamId).orElseThrow(
                () -> new ChatException(ChatErrorCode.ROOM_NOT_FOUND));

        String key = prefix + "room:" + teamId;

        Set<ChatMessage> messageSet = redisTemplateForMessage.opsForZSet().reverseRange(key, 0, 99);
        List<ChatMessage> messages = List.copyOf(messageSet);

        List<ChatMessageResponse> messageDtos = messages.stream()
                .map(message -> new ChatMessageResponse(message.getUserId(), message.getName(), message.getMessage(), message.getCreatedAt()))
                .toList();
        return messageDtos;

    }

    @CacheEvict(value = "chatMessages", key = "#messageDto.teamId")
    public void sendMessage(ChatMessage messageDto) {

        String key = prefix + "room:" + messageDto.getTeamId();

        Long generatedId = redisTemplateForMessage.opsForValue().increment("chatMessage:id");
        int id = generatedId != null ? generatedId.intValue() : 0;
        ChatMessage updatedMessageDto = messageDto.toBuilder()
                .id(id)
                .createdAt(LocalDateTime.now())
                .build();

        long score = updatedMessageDto.getCreatedAt().toEpochSecond(ZoneOffset.UTC);
        redisTemplateForMessage.opsForZSet().add(key, updatedMessageDto, score);

        messageTemplate.convertAndSend("/sub/chat/room/" + messageDto.getTeamId(), updatedMessageDto);
    }
}

