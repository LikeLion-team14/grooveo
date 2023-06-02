package com.kl.grooveo.boundedContext.thumbsUp.repository;

import com.kl.grooveo.boundedContext.thumbsUp.entity.ThumbsUp_summary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ThumbsUp_summaryRepository extends JpaRepository<ThumbsUp_summary, Long> {
    ThumbsUp_summary findByPostId(Long postId);

    boolean existsByPostId(Long postId);
}