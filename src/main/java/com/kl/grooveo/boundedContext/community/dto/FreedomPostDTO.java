package com.kl.grooveo.boundedContext.community.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FreedomPostDTO {
	private Long postId;
	private String title;
	private String authorName;
	private String authorNickname;
	private String categoryDisplayName;
	private String profileImageUrl;
	private String content;
	private LocalDateTime createDate;
	private int commentCnt;
}
