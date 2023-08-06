package com.kl.grooveo.boundedContext.thumbsUp.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kl.grooveo.boundedContext.library.entity.SoundTrack;
import com.kl.grooveo.boundedContext.library.service.SoundTrackService;
import com.kl.grooveo.boundedContext.thumbsUp.dto.ThumbsUpDTO;
import com.kl.grooveo.boundedContext.thumbsUp.entity.SoundThumbsUp_summary;
import com.kl.grooveo.boundedContext.thumbsUp.repository.SoundThumbsUp_summaryRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class SoundThumbsUp_summaryService {
	private final SoundThumbsUp_summaryRepository soundThumbsUpSummaryRepository;
	private final SoundTrackService soundTrackService;

	@Transactional
	public void updateLikeCount(Long postId, int num) {
		SoundTrack soundTrack = soundTrackService.getSoundTrack(postId);
		if (soundTrack == null)
			return;
		SoundThumbsUp_summary soundThumbsUpSummary = soundThumbsUpSummaryRepository.findBySoundTrack(soundTrack);
		if (soundThumbsUpSummary != null) {
			soundThumbsUpSummary.setLikeCount(soundThumbsUpSummary.getLikeCount() + num);
			soundThumbsUpSummaryRepository.save(soundThumbsUpSummary);
		}
	}

	@Transactional
	public ThumbsUpDTO getLikeCount(Long postId, int plusNum) {
		SoundTrack soundTrack = soundTrackService.getSoundTrack(postId);
		SoundThumbsUp_summary soundThumbsUpSummary = soundThumbsUpSummaryRepository.findBySoundTrack(soundTrack);
		if (soundThumbsUpSummary == null) {
			return new ThumbsUpDTO("error", -1);
		}

		String result = (plusNum == 1) ? "unliked" : "liked";
		return new ThumbsUpDTO(result, soundThumbsUpSummary.getLikeCount());
	}

	public int getLikeCount(Long postId) {
		SoundTrack soundTrack = soundTrackService.getSoundTrack(postId);
		SoundThumbsUp_summary soundThumbsUpSummary = soundThumbsUpSummaryRepository.findBySoundTrack(soundTrack);
		if (soundThumbsUpSummary == null) {
			return -1;
		}

		return soundThumbsUpSummary.getLikeCount();
	}
}
