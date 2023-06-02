package com.kl.grooveo.boundedContext.thumbsUp.repository;

import com.kl.grooveo.boundedContext.thumbsUp.entity.ThumbsUp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ThumbsUpRepository extends JpaRepository<ThumbsUp, Long> {
    boolean existsByPostIdAndMemberId(Long postId, Long memberId);

    Optional<ThumbsUp> findByPostIdAndMemberId(Long postId, Long memberId);
}
