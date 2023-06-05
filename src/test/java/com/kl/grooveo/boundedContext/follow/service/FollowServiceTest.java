package com.kl.grooveo.boundedContext.follow.service;

import com.kl.grooveo.boundedContext.follow.entity.Follow;
import com.kl.grooveo.boundedContext.follow.repository.FollowRepository;
import com.kl.grooveo.boundedContext.member.entity.Member;
import com.kl.grooveo.boundedContext.member.repository.MemberRepository;
import com.kl.grooveo.boundedContext.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@TestMethodOrder(MethodOrderer.MethodName.class)
class FollowServiceTest {

    @Autowired
    private FollowService followService;
    @Autowired
    private FollowRepository followRepository;
    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("following Test")
    void test001() {
        Member member1 = memberRepository.findById(1L).get();
        Member member2 = memberRepository.findById(2L).get();

        followService.following(member1, member2);

        Optional<Follow> follow = followRepository.findByFollower_usernameAndFollowing_username(
                member1.getUsername(), member2.getUsername());


        assertEquals(true, follow.isPresent());
    }

    @Test
    @DisplayName("unFollowing Test")
    void test002() throws Exception {
        Member member1 = memberRepository.findById(1L).get();
        Member member2 = memberRepository.findById(2L).get();

        followService.following(member1, member2);

        Optional<Follow> follow = followRepository.findByFollower_usernameAndFollowing_username(
                member1.getUsername(), member2.getUsername());


        assertEquals(true, follow.isPresent());

        followService.unFollowing(member1, member2);

        Optional<Follow> unFollow = followRepository.findByFollower_usernameAndFollowing_username(
                member1.getUsername(), member2.getUsername());

        assertEquals(true, unFollow.isEmpty());
    }
}