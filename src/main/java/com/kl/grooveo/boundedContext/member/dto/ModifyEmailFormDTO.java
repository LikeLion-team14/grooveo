package com.kl.grooveo.boundedContext.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModifyEmailFormDTO {

	@NotBlank
	@Email
	@Size(min = 3, max = 30)
	private String email;
}