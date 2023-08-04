package com.kl.grooveo.boundedContext.library.service;

import static java.util.stream.Collectors.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kl.grooveo.base.event.EventAfterUpload;
import com.kl.grooveo.boundedContext.follow.entity.Follow;
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
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class SoundTrackService {
	private final SoundTrackRepository soundTrackRepository;
	private final MemberRepository memberRepository;
	private final SoundThumbsUpService soundThumbsUpService;
	private final ApplicationEventPublisher publisher;

	public SoundTrackService(SoundTrackRepository soundTrackRepository, MemberRepository memberRepository,
		@Lazy SoundThumbsUpService soundThumbsUpService, ApplicationEventPublisher publisher) {
		this.soundTrackRepository = soundTrackRepository;
		this.memberRepository = memberRepository;
		this.soundThumbsUpService = soundThumbsUpService;
		this.publisher = publisher;
	}

	public void saveSoundTrack(SoundTrack soundTrack) {
		Member actor = soundTrack.getArtist();
		List<Follow> followerList = actor.getFollowingPeople();

		for (Follow follower : followerList) {
			publisher.publishEvent(new EventAfterUpload(this, follower));
		}

		soundTrackRepository.save(soundTrack);
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

	// 조회수 카운트
	public void updateView(Long id) {
		soundTrackRepository.updateView(id);
	}

	public int getViewCnt(Long postId) {
		Optional<SoundTrack> soundTrack = soundTrackRepository.findById(postId);
		return soundTrack.map(SoundTrack::getView).orElse(-1);
	}

	@Transactional
	public void updateViewCount(HttpServletRequest request, HttpServletResponse response, Long id) {
		// 조회수 관련 로직
		Cookie oldCookie = null;
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {   // 쿠키가 null 인지 검사
			// null 이 아니라면 "soundPostView" 라는 이름의 쿠키가 있는지 검사
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("soundPostView")) {
					oldCookie = cookie;
				}
			}
		}

		if (oldCookie != null) {
			// "soundPostView" 가 존재한다면
			// value 가 현재 접근한 게시글의 id 를 포함하고 있는지 검사
			if (!oldCookie.getValue().contains("[" + id.toString() + "]")) {
				// 포함하고 있지 않으면 조회수 증가
				updateView(id);
				oldCookie.setValue(oldCookie.getValue() + "_[" + id + "]");
				oldCookie.setPath("/");
				oldCookie.setMaxAge(60 * 60 * 24);                            // 쿠키 시간
				response.addCookie(oldCookie);
			}
		} else {
			// "soundPostView" 가 존재하지 않는다면
			// 게시글의 id 를 포함하는 쿠키를 만들고
			// 마찬가지로 조회수 증가
			updateView(id);
			Cookie newCookie = new Cookie("soundPostView", "[" + id + "]");
			newCookie.setPath("/");
			newCookie.setMaxAge(60 * 60 * 24);                                // 쿠키 시간
			response.addCookie(newCookie);
		}
	}
}