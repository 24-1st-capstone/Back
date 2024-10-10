package com.capstone.usa.chat.service;

import com.capstone.usa.chat.dto.MessageDto;
import com.capstone.usa.chat.model.ChatMessage;
import com.capstone.usa.chat.model.ChatRoom;
import com.capstone.usa.chat.repository.ChatMessageRepository;
import com.capstone.usa.chat.repository.ChatRoomRepository;
import com.capstone.usa.auth.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository ChatMessageRepository;

    @Transactional
    public void saveMessage(User user, MessageDto dto) {
        Optional<ChatRoom> chatRoomOptional = chatRoomRepository.findByRoomId(dto.getChatRoomId());
        ChatRoom chatRoom = chatRoomOptional.get();

        ChatMessage chat = new ChatMessage(
                0L,
                chatRoom,
                user.getName(),
                dto.getMessage(),
                LocalDateTime.now()
        );
        ChatMessageRepository.save(chat);
    }
}
