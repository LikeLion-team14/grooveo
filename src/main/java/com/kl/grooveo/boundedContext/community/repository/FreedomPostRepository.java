package com.kl.grooveo.boundedContext.community.repository;

import com.kl.grooveo.boundedContext.community.entity.FreedomPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FreedomPostRepository extends JpaRepository<FreedomPost, Long> {
        Page<FreedomPost> findAll(Specification<FreedomPost> spec, Pageable pageable);
}
