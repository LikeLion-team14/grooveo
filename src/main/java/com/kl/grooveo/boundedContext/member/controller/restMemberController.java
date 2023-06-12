package com.kl.grooveo.boundedContext.member.controller;

import com.kl.grooveo.base.rq.Rq;
import com.kl.grooveo.boundedContext.follow.service.FollowService;
import com.kl.grooveo.boundedContext.member.dto.MemberProfileDto;
import com.kl.grooveo.boundedContext.member.entity.Member;
import com.kl.grooveo.boundedContext.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class restMemberController {

    private final MemberService memberService;
    private final FollowService followService;
    private final Rq rq;

    @GetMapping("api/member/getProfile")
    @ResponseBody
    public MemberProfileDto getProfile(@RequestParam String username) throws Exception {
        Optional<Member> opFollowingUser = memberService.findByUsername(username);

        if (opFollowingUser.isEmpty()) {
            throw new Exception("존재하지 않는 User 입니다.");
        }

        Member followUser = rq.getMember();
        Member followingUser = opFollowingUser.get();

        boolean isFollowing = followService.isFollowing(followUser, followingUser);

        return MemberProfileDto.builder()
                .profileImageUrl(followingUser.getProfileImageUrl())
                .username(followingUser.getUsername())
                .postCount(followingUser.getFreedomPosts().size())
                .followerPeopleCount(followingUser.getFollowerPeople().size())
                .followingPeopleCount(followingUser.getFollowingPeople().size())
                .nickName(followingUser.getNickName())
                .email(followingUser.getEmail())
                .isFollowing(isFollowing)
                .build();
    }
}