package com.capstone.usa.chat.service;

import com.capstone.usa.article.model.Article;
import com.capstone.usa.article.service.ArticleService;
import com.capstone.usa.chat.dto.ChatRoomIdDto;
import com.capstone.usa.chat.dto.UserNameDto;
import com.capstone.usa.chat.model.ChatMessage;
import com.capstone.usa.chat.model.ChatRoom;
import com.capstone.usa.chat.repository.ChatMessageRepository;
import com.capstone.usa.chat.repository.ChatRoomRepository;
import com.capstone.usa.job.model.Job;
import com.capstone.usa.job.service.JobService;
import com.capstone.usa.auth.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private static final String CHAT_TYPE_JOB = "job";
    private static final String CHAT_TYPE_ARTICLE = "article";

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final JobService jobService;
    private final ArticleService articleService;

    @Transactional
    public ResponseEntity<?> createChatRoom(String chatType, Long referenceId, User user) {
        return switch (chatType) {
            case CHAT_TYPE_JOB -> createJobChatRoom(referenceId, user);
            case CHAT_TYPE_ARTICLE -> createArticleChatRoom(referenceId, user);
            default -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body("지원하지 않는 채팅 타입입니다");
        };
    }

    private ResponseEntity<?> createJobChatRoom(Long referenceId, User user) {
        Job job = jobService.getJob(referenceId).getBody();
        if (job == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("유효하지 않은 요청입니다");
        }
        if (user.getId().equals(job.getUser().getId())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("자신과의 채팅은 할 수 없습니다");
        }
        return handleChatRoomCreation(job.getUser(), user, CHAT_TYPE_JOB, referenceId);
    }

    private ResponseEntity<?> createArticleChatRoom(Long referenceId, User user) {
        Article article = articleService.getArticle(referenceId).getBody();
        if (article == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("유효하지 않은 요청입니다");
        }
        if (user.getId().equals(article.getUser().getId())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("자신과의 채팅은 할 수 없습니다");
        }
        return handleChatRoomCreation(article.getUser(), user, CHAT_TYPE_ARTICLE, referenceId);
    }

    public ResponseEntity<?> handleChatRoomCreation(User postOwner, User user, String chatType, Long referenceId) {
        Optional<ChatRoom> optionalChatRoom = chatRoomRepository.findByChatTypeAndReferenceIdAndPostOwnerAndUser(chatType, referenceId, postOwner, user);
        if (optionalChatRoom.isEmpty()) {
            ChatRoom chatRoom = new ChatRoom(
                    null,
                    chatType,
                    referenceId,
                    postOwner,
                    user,
                    UUID.randomUUID().toString(),
                    LocalDateTime.now());
            chatRoomRepository.save(chatRoom);
            return ResponseEntity.status(HttpStatus.OK).body(new ChatRoomIdDto(chatRoom.getRoomId(), "", ""));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ChatRoomIdDto(optionalChatRoom.get().getRoomId(), "", ""));
    }

    @Transactional(readOnly = true)
    public List<ChatMessage> getChatMessagesByRoomId(String roomId) {
        Optional<ChatRoom> optionalChatRoom = chatRoomRepository.findByRoomId(roomId);
        if (optionalChatRoom.isPresent()) {
            ChatRoom chatRoom = optionalChatRoom.get();
            return chatMessageRepository.findByChatRoomId(chatRoom.getId());
        }
        throw new IllegalArgumentException("채팅방을 찾을 수 없습니다");
    }

    @Transactional(readOnly = true)
    public List<ChatRoomIdDto> getChatRoomsForUser(User user) {
        List<ChatRoom> chatRooms = chatRoomRepository.findByPostOwnerOrUser(user, user);
        return chatRooms.stream()

                .map(chatRoom -> {
                    String title = getChatRoomTitle(chatRoom);
                    String userName;
                    if (chatRoom.getPostOwner().getId().equals(user.getId())) {
                        userName = chatRoom.getUser().getUsername();
                    } else {
                        userName = chatRoom.getPostOwner().getUsername();
                    }
                    return new ChatRoomIdDto(chatRoom.getRoomId(), title, userName);
                })
                .collect(Collectors.toList());
    }

    public String getChatRoomTitle(ChatRoom chatRoom) {
        if (CHAT_TYPE_JOB.equals(chatRoom.getChatType())) {
            Job job = jobService.getJob(chatRoom.getReferenceId()).getBody();
            return job != null ? job.getTitle() : "알 수 없는 채팅 제목";
        } else if (CHAT_TYPE_ARTICLE.equals(chatRoom.getChatType())) {
            Article article = articleService.getArticle(chatRoom.getReferenceId()).getBody();
            return article != null ? article.getTitle() : "알 수 없는 채팅 제목";
        }
        return "알 수 없는 제목";
    }

    public UserNameDto getUserName(User user) {
        return new UserNameDto(user.getName());
    }
}
