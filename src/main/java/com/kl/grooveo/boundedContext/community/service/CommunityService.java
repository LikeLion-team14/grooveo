package com.kl.grooveo.boundedContext.community.service;

import com.kl.grooveo.boundedContext.community.entity.Community;
import com.kl.grooveo.boundedContext.community.repository.CommunityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CommunityService {
    private final CommunityRepository communityRepository;
    public List<Community> getList() {
        return this.communityRepository.findAll();
    }

    public Community getMoreInformation(Long id) throws Exception{
        Optional<Community> community = this.communityRepository.findById(id);
        if (community.isPresent()) {
            return community.get();
        } else {
            throw new Exception();
        }
    }

    public void create(String title, String category, String content, String user) {
        Community community = new Community();
        community.setTitle(title);
        community.setContent(content);
        community.setAuthor(user);
        community.setCreateDate(LocalDateTime.now());
        community.setCategory(category);
        this.communityRepository.save(community);
    }
}
