package com.example.chatting.service;

import com.example.chatting.domain.ChatMessage;
import com.example.chatting.domain.ChatRoom;
import com.example.chatting.repository.ChatMsRepository;
import com.example.chatting.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMsRepository chatMessageRepository;
    private final SimpMessageSendingOperations messageTemplate;

//    public ChatRoom selectChatRoom(Long roomId) throws Exception {
//        return chatRoomRepository.findById(roomId)
//                .orElseThrow(() -> new Exception("Chat room not found with id: " + roomId));
//    }

    @Transactional
    public void createChatRoom(Long groupId) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setGroupId(groupId);
        chatRoomRepository.save(chatRoom);
    }

    public List<ChatMessage> selectChatMessageList(Long groupId) {
        return chatMessageRepository.findByGroupId(groupId);
    }

    public void sendMessage(ChatMessage messageDto) {

        messageTemplate.convertAndSend("/sub/chat/room/"+messageDto.getGroupId(), messageDto);
        chatMessageRepository.save(messageDto);
    }
}
