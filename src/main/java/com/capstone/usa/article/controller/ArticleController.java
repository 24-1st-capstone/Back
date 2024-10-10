package com.capstone.usa.article.controller;

import com.capstone.usa.article.dto.ArticleDto;
import com.capstone.usa.article.model.Article;
import com.capstone.usa.article.service.ArticleService;
import com.capstone.usa.auth.model.User;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/articles")
@AllArgsConstructor
public class ArticleController {
    private ArticleService articleService;

    @GetMapping
    public List<Article> getArticles() {
        return articleService.getArticles();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Article> getArticle(
            @PathVariable Long id
    ) {
        return articleService.getArticle(id);
    }

    @PostMapping
    public void createArticle(
            @AuthenticationPrincipal User user,
            @RequestPart ArticleDto dto,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) throws IOException {
        articleService.createArticle(user, dto, image);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> modifyArticle(
            @PathVariable Long id,
            @AuthenticationPrincipal User user,
            @RequestBody ArticleDto dto
    ) {
        return articleService.modifyArticle(id, user, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteArticle(
            @PathVariable Long id,
            @AuthenticationPrincipal User user
    ) {
        return articleService.deleteArticle(id, user);
    }

    @PostMapping("/{roomId}/rent")
    public ResponseEntity<?> rentArticle(
            @PathVariable String roomId,
            @AuthenticationPrincipal User user
    ) {
        return articleService.rentArticle(roomId, user);
    }

    @GetMapping("/rented")
    public List<Article> getArticlesRentedByUser(
            @AuthenticationPrincipal User user
    ) {
        return articleService.getArticlesByRenter(user);
    }
}
