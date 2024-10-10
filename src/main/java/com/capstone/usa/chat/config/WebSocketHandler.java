package com.capstone.usa.chat.config;

import com.capstone.usa.chat.dto.ChatMessageDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketHandler extends TextWebSocketHandler {
    private final ObjectMapper mapper;

    private final Map<String, Set<WebSocketSession>> chatRoomSessionMap = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("{} 연결됨", session.getId());
        String chatRoomId = getChatRoomId(session);
        chatRoomSessionMap.computeIfAbsent(chatRoomId, key -> ConcurrentHashMap.newKeySet()).add(session);
    }

    @Override
    protected void handleTextMessage(@NotNull WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("payload {}", payload);

        ChatMessageDto chatMessageDto = mapper.readValue(payload, ChatMessageDto.class);
        log.info("session {}", chatMessageDto.toString());

        String chatRoomId = chatMessageDto.getChatRoomId();
        Set<WebSocketSession> chatRoomSessions = chatRoomSessionMap.get(chatRoomId);

        if (chatRoomSessions != null) {
            removeClosedSessions(chatRoomSessions);
            sendMessageToChatRoom(chatMessageDto, chatRoomSessions);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, @NotNull CloseStatus status) {
        log.info("{} 연결 끊김", session.getId());
        String chatRoomId = getChatRoomId(session);
        Set<WebSocketSession> chatRoomSessions = chatRoomSessionMap.get(chatRoomId);

        if (chatRoomSessions != null) {
            chatRoomSessions.remove(session);
        }
    }

    private void removeClosedSessions(Set<WebSocketSession> chatRoomSessions) {
        chatRoomSessions.removeIf(sess -> !sess.isOpen());
    }

    private void sendMessageToChatRoom(ChatMessageDto chatMessageDto, Set<WebSocketSession> chatRoomSessions) {
        chatRoomSessions.parallelStream().forEach(sess -> sendMessage(sess, chatMessageDto));
    }

    private <T> void sendMessage(WebSocketSession session, T message) {
        try {
            session.sendMessage(new TextMessage(mapper.writeValueAsString(message)));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getChatRoomId(WebSocketSession session) {
        String path = Objects.requireNonNull(session.getUri()).getPath();
        return path.split("/")[path.split("/").length - 1];
    }
}
