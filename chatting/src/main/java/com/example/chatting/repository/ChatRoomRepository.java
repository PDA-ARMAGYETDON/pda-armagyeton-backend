package com.example.chatting.repository;

import com.example.chatting.domain.ChatRoom;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ChatRoomRepository extends CrudRepository<ChatRoom, Integer> {
    Optional<ChatRoom> findByTeamId(int teamId);
}
