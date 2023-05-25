package com.kl.grooveo.boundedContext.member.entity;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JoinForm {

    @NotBlank
    @Size(min = 3, max = 30)
    private String memberId;

    @NotBlank
    @Size(min = 3, max = 30)
    private String memberPassword;

    @NotBlank
    @Size(min = 3, max = 30)
    private String confirmPassword;

    @NotBlank
    @Size(min = 2, max = 10)
    private String name;

    @NotBlank
    @Size(min = 2, max = 10)
    private String nickName;

    @NotBlank
    @Email
    @Size(min = 3, max = 30)
    private String email;

}
