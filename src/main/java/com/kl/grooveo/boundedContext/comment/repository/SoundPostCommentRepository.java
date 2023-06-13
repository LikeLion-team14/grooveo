package com.kl.grooveo.boundedContext.comment.repository;

import com.kl.grooveo.boundedContext.comment.entity.SoundPostComment;
import com.kl.grooveo.boundedContext.library.entity.FileInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SoundPostCommentRepository extends JpaRepository<SoundPostComment, Long> {
    Page<SoundPostComment> findAllByFileInfo(FileInfo fileInfo, Pageable pageable);

    Page<SoundPostComment> findAllByAuthorId(Long username, Pageable pageable);
}
