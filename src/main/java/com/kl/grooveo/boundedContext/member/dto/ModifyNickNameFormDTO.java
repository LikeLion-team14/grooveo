package com.kl.grooveo.boundedContext.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModifyNickNameFormDTO {

	@NotBlank
	@Size(min = 2, max = 10)
	private String nickName;
}