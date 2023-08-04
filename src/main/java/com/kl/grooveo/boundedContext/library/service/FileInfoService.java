package com.kl.grooveo.boundedContext.library.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.kl.grooveo.boundedContext.library.dto.FileInfoDTO;
import com.kl.grooveo.boundedContext.library.entity.SoundTrack;
import com.kl.grooveo.boundedContext.library.repository.SoundTrackRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FileInfoService {

	private final SoundTrackRepository soundTrackRepository;

	private List<SoundTrack> getLatestSongs() {
		return soundTrackRepository.findTop10ByOrderByCreateDateDesc();
	}

	public List<FileInfoDTO> convertLatestSongsToDTO() {
		return getLatestSongs().stream().map(this::convertToFileInfoDTO).collect(Collectors.toList());
	}

	private List<SoundTrack> getPopularSongs() {
		return soundTrackRepository.findTop10ByHighestLikeCount();
	}

	private FileInfoDTO convertToFileInfoDTO(SoundTrack soundTrack) {
		int soundThumbsUpSummaryCount =
			(soundTrack.getSoundThumbsUpSummary() != null) ? soundTrack.getSoundThumbsUpSummary().getLikeCount() : 0;

		return FileInfoDTO.builder()
			.id(soundTrack.getId())
			.albumCoverUrl(soundTrack.getAlbumCoverUrl())
			.title(soundTrack.getTitle())
			.artistNickname(soundTrack.getArtist().getNickName())
			.soundThumbsUpSummary(soundTrack.getSoundThumbsUpSummary())
			.soundThumbsUpSummaryCount(soundThumbsUpSummaryCount)
			.build();
	}

	public List<FileInfoDTO> convertToPopularSongTop10DTO() {
		return getPopularSongs().stream().map(this::convertToFileInfoDTO).collect(Collectors.toList());
	}
}