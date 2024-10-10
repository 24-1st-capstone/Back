package com.capstone.usa.chat.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
public class CustomHandshakeInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            @NotNull ServerHttpResponse response,
            @NotNull WebSocketHandler wsHandler,
            @NotNull Map<String, Object> attributes
    ) {
        String path = request.getURI().getPath();
        String[] pathSegments = path.split("/");
        if (pathSegments.length >= 2) {
            String chatRoomId = pathSegments[pathSegments.length - 1];
            attributes.put("chatRoomId", chatRoomId);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void afterHandshake(
            @NotNull ServerHttpRequest request,
            @NotNull ServerHttpResponse response,
            @NotNull WebSocketHandler wsHandler,
            Exception exception
    ) {

    }
}
