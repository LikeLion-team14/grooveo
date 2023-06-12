package com.kl.grooveo.boundedContext.library.repository;

import com.kl.grooveo.boundedContext.library.entity.FileInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileInfoRepository extends JpaRepository<FileInfo, Long> {
    Page<FileInfo> findAll(Specification<FileInfo> spec, Pageable pageable);
}