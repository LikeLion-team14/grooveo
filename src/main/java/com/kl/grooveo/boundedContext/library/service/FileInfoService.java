package com.kl.grooveo.boundedContext.library.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.kl.grooveo.base.event.EventAfterUpload;
import com.kl.grooveo.boundedContext.follow.entity.Follow;
import com.kl.grooveo.boundedContext.follow.service.FollowService;
import com.kl.grooveo.boundedContext.library.dto.FileInfoDTO;
import com.kl.grooveo.boundedContext.library.entity.FileInfo;
import com.kl.grooveo.boundedContext.library.repository.FileInfoRepository;
import com.kl.grooveo.boundedContext.member.entity.Member;

@Service
public class FileInfoService {

	private final FileInfoRepository fileInfoRepository;
	private final ApplicationEventPublisher publisher;

	public FileInfoService(FileInfoRepository fileInfoRepository, ApplicationEventPublisher publisher,
		FollowService followService) {
		this.fileInfoRepository = fileInfoRepository;
		this.publisher = publisher;
	}

	public FileInfo saveFileInfo(FileInfo fileInfo) {
		Member actor = fileInfo.getArtist();
		List<Follow> followerList = actor.getFollowingPeople();

		for (Follow follower : followerList) {
			publisher.publishEvent(new EventAfterUpload(this, follower));
			System.out.println(follower.getFollower().getUsername());
		}

		return fileInfoRepository.save(fileInfo);
	}

	public FileInfo findById(Long id) {
		Optional<FileInfo> optionalFileInfo = fileInfoRepository.findById(id);
		return optionalFileInfo.orElse(null);
	}

	public FileInfo getFileInfo(Long id) {
		Optional<FileInfo> optionalFileInfo = fileInfoRepository.findById(id);
		return optionalFileInfo.get();
	}

	public void modify(FileInfo fileInfo, String description) {
		fileInfo.setDescription(description);
		fileInfo.setModifyDate(LocalDateTime.now());
		this.fileInfoRepository.save(fileInfo);
	}

	public void delete(Long id) {
		FileInfo fileInfo = getFileInfo(id);
		fileInfoRepository.delete(fileInfo);
	}

	public List<FileInfo> getLatestSongs() {
		return fileInfoRepository.findTop10ByOrderByCreateDateDesc();
	}

	private List<FileInfo> getPopularSongs() {
		return fileInfoRepository.findTop10ByHighestLikeCount();
	}

	private FileInfoDTO convertToPopularSongDTO(FileInfo fileInfo) {
		return FileInfoDTO.builder()
			.id(fileInfo.getId())
			.albumCoverUrl(fileInfo.getAlbumCoverUrl())
			.title(fileInfo.getTitle())
			.artistNickname(fileInfo.getArtist().getNickName())
			.soundThumbsUpSummary(fileInfo.getSoundThumbsUpSummary())
			.thumbsUpSummaryCount(fileInfo.getSoundThumbsUpSummary().getLikeCount())
			.build();
	}

	public List<FileInfoDTO> convertToPopularSongTop10DTO() {
		return getPopularSongs().stream().map(this::convertToPopularSongDTO).collect(Collectors.toList());
	}
}