package com.example.chatting.service;

import com.example.chatting.domain.ChatMessage;
import com.example.chatting.domain.ChatRoom;
import com.example.chatting.repository.ChatMsRepository;
import com.example.chatting.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMsRepository chatMessageRepository;
    private final SimpMessageSendingOperations messageTemplate;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
//    public ChatRoom selectChatRoom(Long roomId) throws Exception {
//        return chatRoomRepository.findById(roomId)
//                .orElseThrow(() -> new Exception("Chat room not found with id: " + roomId));
//    }

    @Transactional
    public void createChatRoom(int teamId) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setTeamId(teamId);
        chatRoomRepository.save(chatRoom);
    }

    public List<ChatMessage> selectChatMessageList(int teamId) {
        return chatMessageRepository.findByTeamId(teamId);
    }

    public void sendMessage(ChatMessage messageDto) {

        messageTemplate.convertAndSend("/sub/chat/room/"+messageDto.getGroupId(), messageDto);
        chatMessageRepository.save(messageDto);
    }
}
