package com.kl.grooveo.boundedContext.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberProfileDto {

    private String username;
    private int followerPeopleCount;
    private int followingPeopleCount;
    private String nickName;
    private String email;
    private boolean isFollowing;
}