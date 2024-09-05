package com.example.chatting.service;

import com.example.chatting.domain.ChatMessage;
import com.example.chatting.domain.ChatRoom;
import com.example.chatting.repository.ChatMsRepository;
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
    private final ChatMsRepository chatMessageRepository;
    private final SimpMessageSendingOperations messageTemplate;
    private final RedisTemplate<String, ChatMessage> redisTemplateForMessage;  // 수정된 부분

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
    public List<ChatMessage> selectChatMessageList(int teamId) {
        String key = prefix + "room:" + teamId;

        try {
            Set<ChatMessage> messageSet = redisTemplateForMessage.opsForZSet().reverseRange(key, 0, 99);

            if (messageSet != null && !messageSet.isEmpty()) {
                log.info("캐시 히트: Redis에서 데이터를 가져왔습니다.");
            } else {
                log.info("캐시 미스: 데이터를 캐시에서 가져오지 못했습니다. Redis에서 데이터를 가져옵니다.");
            }

            log.info("Redis에서 가져온 메시지 수: {}", messageSet.size());
            return List.copyOf(messageSet);

        } catch (Exception e) {
            log.error("Redis에서 메시지를 가져오는 중 오류 발생: {}", e.getMessage(), e);
            throw e;  // 적절한 예외 처리 로직 추가
        }
    }

    @CacheEvict(value = "chatMessages", key = "#messageDto.teamId")
    public void sendMessage(ChatMessage messageDto) {

        String key = prefix + "room:" + messageDto.getTeamId();
        log.info("Redis에 메시지 저장 시작 - 팀 ID: {}, 키: {}", messageDto.getTeamId(), key);

        Long generatedId = redisTemplateForMessage.opsForValue().increment("chatMessage:id");
        int id = generatedId != null ? generatedId.intValue() : 0;
        ChatMessage updatedMessageDto = messageDto.toBuilder()
                .id(id)
                .createdAt(LocalDateTime.now())
                .build();

        long score = updatedMessageDto.getCreatedAt().toEpochSecond(ZoneOffset.UTC);
        redisTemplateForMessage.opsForZSet().add(key, updatedMessageDto, score);
        log.info("Redis에 메시지 저장 완료. 키: {}, 메시지 ID: {}", key, id);

        messageTemplate.convertAndSend("/sub/chat/room/" + messageDto.getTeamId(), updatedMessageDto);
        log.info("메시지가 전송되었습니다. 팀 ID: {}", messageDto.getTeamId());
    }
}
