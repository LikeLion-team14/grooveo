package com.kl.grooveo.boundedContext.comment.repository;

import com.kl.grooveo.boundedContext.comment.entity.FreedomPostComment;
import com.kl.grooveo.boundedContext.community.entity.FreedomPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FreedomPostCommentRepository extends JpaRepository<FreedomPostComment, Long> {
    Page<FreedomPostComment> findAllByFreedomPost(FreedomPost freedomPost, Pageable pageable);
}
