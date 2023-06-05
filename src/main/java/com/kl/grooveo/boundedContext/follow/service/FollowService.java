package com.kl.grooveo.boundedContext.follow.service;

import com.kl.grooveo.base.rsData.RsData;
import com.kl.grooveo.boundedContext.follow.entity.Follow;
import com.kl.grooveo.boundedContext.follow.repository.FollowRepository;
import com.kl.grooveo.boundedContext.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FollowService {

    private final FollowRepository followRepository;

    @Transactional
    public void following(Member follower, Member following) {
        Follow follow = Follow
                .builder()
                .follower(follower)
                .following(following)
                .build();

        followRepository.save(follow);
    }

    @Transactional
    public void unFollowing(Member follower, Member following) throws Exception {
        Optional<Follow> follow = followRepository.findByFollower_usernameAndFollowing_username(follower.getUsername(), following.getUsername());
        if (follow.isEmpty()) {
            throw new Exception("해당 Follow가 존재하지 않습니다.");
        }

        followRepository.delete(follow.get());
    }
}