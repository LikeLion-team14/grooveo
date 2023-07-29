package com.kl.grooveo.boundedContext.community.entity;

public enum Category {
	ALL("전체", "all"),
	MUSIC("음악", "music"),
	REVIEW("리뷰", "review"),
	LYRICS("가사 해석", "lyrics"),
	CERTIFY("인증/후기", "certify");

	private final String displayName;
	private final String code;

	Category(String displayName, String code) {
		this.displayName = displayName;
		this.code = code;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getCode() {
		return code;
	}
}

