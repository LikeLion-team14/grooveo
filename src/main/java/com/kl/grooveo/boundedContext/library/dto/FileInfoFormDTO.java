package com.kl.grooveo.boundedContext.library.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileInfoFormDTO {
	@NotEmpty(message = "The description is required.")
	private String description;
}
