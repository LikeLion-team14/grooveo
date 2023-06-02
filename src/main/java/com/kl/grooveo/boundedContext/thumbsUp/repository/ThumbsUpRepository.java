package com.kl.grooveo.boundedContext.thumbsUp.repository;

import com.kl.grooveo.boundedContext.member.entity.Member;
import com.kl.grooveo.boundedContext.thumbsUp.entity.ThumbsUp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ThumbsUpRepository extends JpaRepository<ThumbsUp, Long> {
    boolean existsByPostIdAndMember(Long postId, Member member);

    Optional<ThumbsUp> findByPostIdAndMember(Long postId, Member member);
}
