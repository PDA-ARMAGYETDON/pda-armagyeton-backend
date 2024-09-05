package com.example.chatting.repository;

import com.example.chatting.domain.ChatRoom;
import org.springframework.data.repository.CrudRepository;

public interface ChatRoomRepository extends CrudRepository<ChatRoom, Integer> {
    //ChatRoom findChatRoomById(Long id);
}
