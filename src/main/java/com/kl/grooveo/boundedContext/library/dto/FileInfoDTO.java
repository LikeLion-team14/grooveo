package com.kl.grooveo.boundedContext.library.dto;

import com.kl.grooveo.boundedContext.thumbsUp.entity.SoundThumbsUp_summary;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FileInfoDTO {
	private Long id;
	private String albumCoverUrl;
	private String title;
	private String artistNickname;
	private SoundThumbsUp_summary soundThumbsUpSummary;
	private int thumbsUpSummaryCount;
}