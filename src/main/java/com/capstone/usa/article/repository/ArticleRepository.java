package com.capstone.usa.article.repository;

import com.capstone.usa.article.model.Article;
import com.capstone.usa.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    List<Article> findByRentedBy(User user);
}
