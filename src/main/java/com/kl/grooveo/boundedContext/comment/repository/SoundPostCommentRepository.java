package com.kl.grooveo.boundedContext.comment.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kl.grooveo.boundedContext.comment.entity.SoundPostComment;
import com.kl.grooveo.boundedContext.library.entity.SoundTrack;

@Repository
public interface SoundPostCommentRepository extends JpaRepository<SoundPostComment, Long> {
	Page<SoundPostComment> findAllBySoundTrack(SoundTrack soundTrack, Pageable pageable);

	Page<SoundPostComment> findAllByAuthorId(Long username, Pageable pageable);
}
