package com.capstone.usa.chat.controller;

import com.capstone.usa.chat.dto.ChatRoomIdDto;
import com.capstone.usa.chat.dto.UserNameDto;
import com.capstone.usa.chat.dto.MessageDto;
import com.capstone.usa.chat.model.ChatMessage;
import com.capstone.usa.chat.service.ChatMessageService;
import com.capstone.usa.chat.service.ChatRoomService;
import com.capstone.usa.auth.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;

    @PostMapping("/create/{chatType}/{referenceId}")
    public ResponseEntity<?> createChatRoom(
            @PathVariable String chatType,
            @PathVariable Long referenceId,
            @AuthenticationPrincipal User user
    ) {
        return chatRoomService.createChatRoom(chatType, referenceId, user);
    }

    @PostMapping("/sendMessage")
    public void sendMessage(
            @RequestBody MessageDto dto,
            @AuthenticationPrincipal User user
    ) {
        chatMessageService.saveMessage(user, dto);
    }

    @GetMapping("/messages/{roomId}")
    public ResponseEntity<List<ChatMessage>> getChatMessages(
            @PathVariable String roomId
    ) {
        List<ChatMessage> messages = chatRoomService.getChatMessagesByRoomId(roomId);
        return ResponseEntity.status(HttpStatus.OK).body(messages);
    }

    @GetMapping("/getUser")
    public UserNameDto getUserName(
            @AuthenticationPrincipal User user
    ) {
        return chatRoomService.getUserName(user);
    }

    @GetMapping("/myRooms")
    public ResponseEntity<List<ChatRoomIdDto>> getMyChatRooms(
            @AuthenticationPrincipal User currentUser
    ) {
        List<ChatRoomIdDto> chatRooms = chatRoomService.getChatRoomsForUser(currentUser);
        return ResponseEntity.status(HttpStatus.OK).body(chatRooms);
    }
}
