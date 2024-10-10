package com.capstone.usa.article.model;

import com.capstone.usa.auth.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "articles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String content;
    @ManyToOne
    private User user;
    private String imgUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @ManyToOne
    private User rentedBy;
    private boolean isRented;
    private LocalDateTime rentAt;
}