package com.kl.grooveo.boundedContext.community.service;

import com.kl.grooveo.boundedContext.community.entity.Community;
import com.kl.grooveo.boundedContext.community.repository.CommunityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CommunityService {
    private final CommunityRepository communityRepository;
    public List<Community> getList() {
        return this.communityRepository.findAll();
    }
}
