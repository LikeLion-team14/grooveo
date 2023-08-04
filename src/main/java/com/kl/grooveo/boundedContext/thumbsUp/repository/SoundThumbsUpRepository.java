package com.kl.grooveo.boundedContext.thumbsUp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kl.grooveo.boundedContext.library.entity.SoundTrack;
import com.kl.grooveo.boundedContext.member.entity.Member;
import com.kl.grooveo.boundedContext.thumbsUp.entity.SoundThumbsUp;

public interface SoundThumbsUpRepository extends JpaRepository<SoundThumbsUp, Long> {
	boolean existsBySoundTrackAndMember(SoundTrack soundTrack, Member member);

	Optional<SoundThumbsUp> findBySoundTrackAndMember(SoundTrack soundTrack, Member member);

	List<SoundThumbsUp> findAllByMemberIdAndSoundTrackIdIn(Long id, long[] ids);
}
