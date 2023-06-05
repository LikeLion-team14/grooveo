package com.kl.grooveo.boundedContext.follow.service;

import com.kl.grooveo.base.exception.DataNotFoundException;
import com.kl.grooveo.boundedContext.follow.entity.Follow;
import com.kl.grooveo.boundedContext.follow.repository.FollowRepository;
import com.kl.grooveo.boundedContext.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        follower.getFollowingPeople().add(follow);
        following.getFollowingPeople().add(follow);

        followRepository.save(follow);
    }

    @Transactional
    public void unFollowing(Member follower, Member following) {
        Optional<Follow> follow = followRepository.findByFollower_usernameAndFollowing_username(follower.getUsername(), following.getUsername());
        if (follow.isEmpty()) {
            throw new DataNotFoundException("해당 Follow가 존재하지 않습니다.");
        }
        follower.getFollowingPeople().remove(follow.get());
        following.getFollowerPeople().remove(follow.get());

        followRepository.delete(follow.get());
    }

    public Optional<Follow> findByFollowerAndFollowing(Member follower, Member following) {
        return followRepository.findByFollowerAndFollowing(follower, following);
    }
}