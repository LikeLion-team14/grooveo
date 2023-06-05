package com.kl.grooveo.boundedContext.follow.repository;

import com.kl.grooveo.boundedContext.follow.entity.Follow;
import com.kl.grooveo.boundedContext.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    Optional<Follow> findByFollower_usernameAndFollowing_username(String followerUsername, String followingUsername);

    Optional<Follow> findByFollowerAndFollowing(Member follower, Member following);
}