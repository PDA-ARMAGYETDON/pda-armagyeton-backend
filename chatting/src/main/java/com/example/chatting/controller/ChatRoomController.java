import com.example.chatting.domain.ChatRoom;
import com.example.chatting.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @PostMapping("/room")
    public ResponseEntity<String> create(@RequestBody ChatRoom chatroom) {
        try {
            chatRoomService.createChatRoom(chatroom.getGroupId());
            return ResponseEntity.status(HttpStatus.CREATED).body("Chat room created");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating chat room");
        }
    }

