package com.kl.grooveo.boundedContext.follow.repository;

import com.kl.grooveo.boundedContext.follow.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow, Long> {
}