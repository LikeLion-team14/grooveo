package com.kl.grooveo.boundedContext.comment.repository;

import com.kl.grooveo.boundedContext.comment.entity.FreedomPostComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FreedomPostCommentRepository extends JpaRepository<FreedomPostComment, Long> {
}
