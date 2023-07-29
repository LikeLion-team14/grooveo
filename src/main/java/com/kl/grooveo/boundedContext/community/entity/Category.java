package com.kl.grooveo.boundedContext.community.entity;

public enum Category {
	ALL("전체", ""),
	MUSIC("음악", "c2"),
	REVIEW("리뷰", "c3"),
	LYRICS("가사 해석", "c4"),
	CERTIFY("인증/후기", "c5");

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

