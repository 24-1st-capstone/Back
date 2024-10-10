package com.capstone.usa.chat.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageDto {
    public enum MessageType{
        CHAT, ENTER, TALK
    }

    private MessageType messageType;
    private String chatRoomId;
    private String sender;
    private Long senderId;
    private String message;
}
