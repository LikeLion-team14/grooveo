package com.kl.grooveo.boundedContext.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModifyPasswordFormDTO {

	@NotBlank
	@Size(min = 3, max = 30)
	private String previousPassword;

	@NotBlank
	@Size(min = 3, max = 30)
	private String newPassword;

	@NotBlank
	@Size(min = 3, max = 30)
	private String confirmNewPassword;
}