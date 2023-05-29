package com.kl.grooveo.boundedContext.community.service;

import com.kl.grooveo.base.exception.DataNotFoundException;
import com.kl.grooveo.boundedContext.community.entity.FreedomPost;
import com.kl.grooveo.boundedContext.community.repository.FreedomPostRepository;
import com.kl.grooveo.boundedContext.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class FreedomPostService {
    private final FreedomPostRepository freedomPostRepository;
    public List<FreedomPost> getList() {
        return this.freedomPostRepository.findAll();
    }

    public void create(String title, String category, String content, Member author) {
        FreedomPost freedomPost = new FreedomPost();
        freedomPost.setTitle(title);
        freedomPost.setContent(content);
        freedomPost.setAuthor(author);
        freedomPost.setCreateDate(LocalDateTime.now());
        freedomPost.setCategory(category);
        this.freedomPostRepository.save(freedomPost);
    }

    public FreedomPost getFreedomPost(Long id) {
        return this.freedomPostRepository.findById(id).orElseThrow(
                () -> new DataNotFoundException("해당 글을 찾을 수 없습니다.")
        );
    }

    public void delete(FreedomPost community) {
        this.freedomPostRepository.delete(community);
    }

    public void modify(FreedomPost freedomPost, String title, String category, String content) {
        freedomPost.setTitle(title);
        freedomPost.setContent(content);
        freedomPost.setModifyDate(LocalDateTime.now());
        freedomPost.setCategory(category);
        this.freedomPostRepository.save(freedomPost);
    }
}