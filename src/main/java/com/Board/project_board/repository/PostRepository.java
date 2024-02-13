package com.Board.project_board.repository;

import com.Board.project_board.entity.Comment;
import com.Board.project_board.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Modifying
    @Query("update Post p set p.view = p.view + 1 where p.id = :id")
    void updateView(@Param("id") Long id);       // JPQL의 id와 매핑하기 위해.

    @Modifying
    @Query("update Post p set p.likeCount = p.likeCount + :value where p.id = :id")
    void updateLikeCount(@Param("id") Long id, @Param("value") int value);

    @Modifying
    @Query("update Post p set p.report = p.report + 1 where p.id = :id")
    void updateReport(@Param("id") Long id);

    Post findByTitle(String title);
    Page<Post> findByTitleContaining(String searchKeyword, Pageable pageable);

    Page<Post> findByUserId(Long user_id, Pageable pageable);
    Page<Post> findByCategoryName(String category_name, Pageable pageable);
    Page<Post> findByCategoryNameAndTitleContaining(String category_name, String searchKeyword, Pageable pageable);
}

