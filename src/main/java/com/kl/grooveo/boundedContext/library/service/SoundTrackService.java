package com.kl.grooveo.boundedContext.library.service;

import static java.util.stream.Collectors.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.kl.grooveo.base.event.EventAfterUpload;
import com.kl.grooveo.base.exception.DataNotFoundException;
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

@Service
public class SoundTrackService {
	private final SoundTrackRepository soundTrackRepository;
	private final MemberRepository memberRepository;
	private final SoundThumbsUpService soundThumbsUpService;
	private final ApplicationEventPublisher publisher;
	private final AmazonS3 amazonS3Client;

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	@Value("${cloud.aws.region.static}")
	private String region;

	public SoundTrackService(SoundTrackRepository soundTrackRepository, MemberRepository memberRepository,
		@Lazy SoundThumbsUpService soundThumbsUpService, ApplicationEventPublisher publisher, AmazonS3 amazonS3Client) {
		this.soundTrackRepository = soundTrackRepository;
		this.memberRepository = memberRepository;
		this.soundThumbsUpService = soundThumbsUpService;
		this.publisher = publisher;
		this.amazonS3Client = amazonS3Client;
	}

	@Transactional
	public Long uploadSoundTrack(SoundTrackFormDTO soundTrackFormDTO, Member artist) throws IOException {
		String albumCoverExtension = FilenameUtils.getExtension(
			soundTrackFormDTO.getAlbumCover().getOriginalFilename());
		String albumCoverName = UUID.randomUUID().toString() + "." + albumCoverExtension;
		String albumCoverUrl =
			"https://s3." + region + ".amazonaws.com/" + bucket + "/albumCover/" + albumCoverName;

		String soundExtension = FilenameUtils.getExtension(soundTrackFormDTO.getSoundFile().getOriginalFilename());
		String soundName = UUID.randomUUID().toString() + "." + soundExtension;
		String soundUrl = "https://s3." + region + ".amazonaws.com/" + bucket + "/sound/" + soundName;

		String titleEncoded = URLEncoder.encode(soundTrackFormDTO.getTitle(), StandardCharsets.UTF_8);
		String descriptionEncoded = URLEncoder.encode(soundTrackFormDTO.getDescription(), StandardCharsets.UTF_8);

		uploadFileToS3(soundTrackFormDTO.getAlbumCover(), "albumCover/" + albumCoverName, titleEncoded,
			descriptionEncoded);
		uploadFileToS3(soundTrackFormDTO.getSoundFile(), "sound/" + soundName, titleEncoded, descriptionEncoded);

		SoundTrack soundTrack = SoundTrack.builder()
			.title(soundTrackFormDTO.getTitle())
			.artist(artist)
			.description(soundTrackFormDTO.getDescription())
			.albumCoverUrl(albumCoverUrl)
			.soundUrl(soundUrl)
			.build();

		saveSoundTrack(soundTrack);

		return soundTrack.getId();
	}

	private void uploadFileToS3(MultipartFile file, String remotePath, String title, String description) throws
		IOException {
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentType(file.getContentType());
		metadata.setContentLength(file.getSize());
		metadata.addUserMetadata("title", title);
		metadata.addUserMetadata("description", description);

		amazonS3Client.putObject(
			new PutObjectRequest(bucket, remotePath, file.getInputStream(), metadata)
		);
	}

	public void saveSoundTrack(SoundTrack soundTrack) {
		Member actor = soundTrack.getArtist();
		List<Follow> followerList = actor.getFollowingPeople();

		soundTrackRepository.save(soundTrack);

		for (Follow follower : followerList) {
			publisher.publishEvent(new EventAfterUpload(this, follower));
		}
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

	public void modifySoundTrack(SoundTrackFormDTO soundTrackFormDTO, Long soundTrackId) throws IOException {
		SoundTrack soundTrack = soundTrackRepository.findById(soundTrackId)
			.orElseThrow(() -> new DataNotFoundException("해당 음원을 찾을 수 없습니다."));

		String[] albumCoverUrl = soundTrack.getAlbumCoverUrl().split("/");
		String albumCoverName = albumCoverUrl[albumCoverUrl.length - 1];

		String[] soundUrl = soundTrack.getSoundUrl().split("/");
		String soundName = soundUrl[soundUrl.length - 1];

		String titleEncoded = URLEncoder.encode(soundTrackFormDTO.getTitle(), StandardCharsets.UTF_8);
		String descriptionEncoded = URLEncoder.encode(soundTrackFormDTO.getDescription(), StandardCharsets.UTF_8);

		uploadFileToS3(soundTrackFormDTO.getAlbumCover(), "albumCover/" + albumCoverName, titleEncoded,
			descriptionEncoded);
		uploadFileToS3(soundTrackFormDTO.getSoundFile(), "sound/" + soundName, titleEncoded, descriptionEncoded);

		soundTrack.updateTitle(soundTrackFormDTO.getTitle());
		soundTrack.updateDescription(soundTrackFormDTO.getDescription());

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
	@Transactional
	public void updateView(Long id) {
		soundTrackRepository.updateView(id);
	}

	public int getViewCnt(Long postId) {
		Optional<SoundTrack> soundTrack = soundTrackRepository.findById(postId);
		return soundTrack.map(SoundTrack::getView).orElse(-1);
	}
}