package com.kl.grooveo.boundedContext.member.entity;

import com.kl.grooveo.base.baseEntity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor
public class Member extends BaseEntity {

    @Column(unique = true)
    private String memberId;
    private String memberPassword;
    private String name;
    @Column(unique = true)
    private String nickName;
    @Column(unique = true)
    private String email;
    @Builder.Default
    private String role = "user";

}
