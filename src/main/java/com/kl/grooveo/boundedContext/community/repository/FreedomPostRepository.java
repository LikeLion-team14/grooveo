package com.kl.grooveo.boundedContext.community.repository;

import com.kl.grooveo.boundedContext.community.entity.FreedomPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FreedomPostRepository extends JpaRepository<FreedomPost, Long> {
        List<FreedomPost> findByBoardTypeAndCategory(Integer boardType, String category);
        List<FreedomPost> findByBoardType(Integer boardType);
}
