package com.kl.grooveo.boundedContext.library.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.kl.grooveo.boundedContext.library.entity.SoundTrack;
import com.kl.grooveo.boundedContext.member.entity.Member;

@Repository
public interface SoundTrackRepository extends JpaRepository<SoundTrack, Long> {
	Page<SoundTrack> findAll(Specification<SoundTrack> spec, Pageable pageable);

	@Modifying
	@Query("update SoundTrack fi set fi.view = fi.view + 1 where fi.id = :id")
	void updateView(@Param("id") Long id);

	Page<SoundTrack> findAllByArtistId(Long username, Pageable pageable);

	List<SoundTrack> findTop10ByOrderByCreateDateDesc();

	Page<SoundTrack> findAllByArtist(Member member, PageRequest pageRequest);

	@Query("SELECT fi " +
		"FROM SoundTrack fi " +
		"JOIN fi.soundThumbsUpSummary stu " +
		"ORDER BY stu.likeCount DESC " +
		"LIMIT 10")
	List<SoundTrack> findTop10ByHighestLikeCount();

	List<SoundTrack> findAllByOrderByIdDesc();

}