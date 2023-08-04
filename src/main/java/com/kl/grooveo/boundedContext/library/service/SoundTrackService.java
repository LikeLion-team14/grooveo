package com.kl.grooveo.boundedContext.library.service;

import static java.util.stream.Collectors.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kl.grooveo.boundedContext.library.dto.SoundTrackFormDTO;
import com.kl.grooveo.boundedContext.library.entity.SoundTrack;
import com.kl.grooveo.boundedContext.library.repository.SoundTrackRepository;
import com.kl.grooveo.boundedContext.member.entity.Member;
import com.kl.grooveo.boundedContext.member.repository.MemberRepository;
import com.kl.grooveo.boundedContext.thumbsUp.entity.SoundThumbsUp;
import com.kl.grooveo.boundedContext.thumbsUp.service.SoundThumbsUpService;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class SoundTrackService {
	private final SoundTrackRepository soundTrackRepository;
	private final MemberRepository memberRepository;
	private final SoundThumbsUpService soundThumbsUpService;

	public SoundTrackService(SoundTrackRepository soundTrackRepository, MemberRepository memberRepository,
		@Lazy SoundThumbsUpService soundThumbsUpService) {
		this.soundTrackRepository = soundTrackRepository;
		this.memberRepository = memberRepository;
		this.soundThumbsUpService = soundThumbsUpService;
	}

	private Specification<SoundTrack> search(String kw) {
		return new Specification<>() {
			private static final long serialVersionUID = 1L;

			@Override
			public Predicate toPredicate(Root<SoundTrack> postRoot, CriteriaQuery<?> query, CriteriaBuilder cb) {
				query.distinct(true);
				Join<SoundTrack, Member> u1 = postRoot.join("artist", JoinType.LEFT);

				return cb.or(cb.like(postRoot.get("title"), "%" + kw + "%"),
					cb.like(u1.get("nickName"), "%" + kw + "%"));
			}
		};
	}

	public Page<SoundTrack> getList(String kw, int page, int sortCode) {
		List<Sort.Order> sorts = new ArrayList<>();

		if (sortCode == 1)
			sorts.add(Sort.Order.desc("soundThumbsUpSummary.likeCount"));
		else
			sorts.add(Sort.Order.desc("createDate"));

		Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
		Specification<SoundTrack> spec = search(kw);
		return soundTrackRepository.findAll(spec, pageable);
	}

	public SoundTrack getSoundTrack(Long id) {
		Optional<SoundTrack> optionalFileInfo = soundTrackRepository.findById(id);
		return optionalFileInfo.orElse(null);
	}

	public void delete(SoundTrack soundTrack) {
		soundTrackRepository.delete(soundTrack);
	}

	public void modify(SoundTrack soundTrack, SoundTrackFormDTO soundTrackFormDTO, String albumCover, String soundUrl) {
		soundTrack.updateTitle(soundTrackFormDTO.getTitle());
		soundTrack.updateDescription(soundTrackFormDTO.getDescription());
		soundTrack.updateSoundUrl(soundUrl);
		soundTrack.updateAlbumCoverUrl(albumCover);

		soundTrackRepository.save(soundTrack);
	}

	// 조회수 카운트
	@Transactional
	public int updateView(Long id) {
		return soundTrackRepository.updateView(id);
	}

	public int getViewCnt(Long postId) {
		Optional<SoundTrack> soundTrack = soundTrackRepository.findById(postId);
		return soundTrack.map(SoundTrack::getView).orElse(-1);
	}

	public Page<SoundTrack> getMemberUploads(String username, int page) {
		Member member = memberRepository.findByUsername(username)
			.orElseThrow(() -> new UsernameNotFoundException("해당 사용자를 찾을 수 없습니다."));

		PageRequest pageRequest = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createDate"));
		return soundTrackRepository.findAllByArtist(member, pageRequest);
	}

	public List<SoundTrack> findAllByOrderByIdDesc() {
		return soundTrackRepository.findAllByOrderByIdDesc();
	}

	public List<SoundTrack> findAllForPrintByOrderByIdDesc(Member actor) {
		List<SoundTrack> soundTracks = findAllByOrderByIdDesc();

		loadForPrintData(soundTracks, actor);

		return soundTracks;
	}

	private void loadForPrintData(List<SoundTrack> soundTracks, Member actor) {
		long[] ids = soundTracks
			.stream()
			.mapToLong(SoundTrack::getId)
			.toArray();

		if (actor != null) {
			List<SoundThumbsUp> soundThumbsUps = soundThumbsUpService.findAllByMemberIdAndSoundTrackIdIn(actor.getId(),
				ids);

			Map<Long, SoundThumbsUp> soundThumbsUpsByProductIdMap = soundThumbsUps
				.stream()
				.collect(toMap(
					soundThumbsUp -> soundThumbsUp.getSoundTrack().getId(),
					soundThumbsUp -> soundThumbsUp
				));

			soundTracks.stream()
				.filter(soundTrack -> soundThumbsUpsByProductIdMap.containsKey(soundTrack.getId()))
				.map(soundTrack -> soundThumbsUpsByProductIdMap.get(soundTrack.getId()))
				.forEach(
					soundThumbsUp -> soundThumbsUp.getSoundTrack().getExtra().put("actor_fileInfo", soundThumbsUp));

		}
	}
}