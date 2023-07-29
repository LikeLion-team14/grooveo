package com.kl.grooveo.boundedContext.thumbsUp.dto;

import lombok.Data;

@Data
public class ThumbsUpDTO {
	private String result;
	private int likeCnt;

	public ThumbsUpDTO(String result, int likeCnt) {
		this.result = result;
		this.likeCnt = likeCnt;
	}
}
