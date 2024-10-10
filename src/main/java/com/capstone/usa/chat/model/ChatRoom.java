package com.capstone.usa.chat.model;

import com.capstone.usa.auth.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_room")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String chatType;
    private Long referenceId;
    @ManyToOne
    private User postOwner;
    @ManyToOne
    private User user;
    @Column(unique = true)
    private String roomId;
    private LocalDateTime createdAt;
}
