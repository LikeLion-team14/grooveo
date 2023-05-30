package com.kl.grooveo.boundedContext.comment.service;

import com.kl.grooveo.base.exception.DataNotFoundException;
import com.kl.grooveo.boundedContext.comment.entity.FreedomPostComment;
import com.kl.grooveo.boundedContext.comment.repository.FreedomPostCommentRepository;
import com.kl.grooveo.boundedContext.community.entity.FreedomPost;
import com.kl.grooveo.boundedContext.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class FreedomPostCommentService {
    private final FreedomPostCommentRepository freedomPostCommentRepository;

    public FreedomPostComment create(FreedomPost freedomPost, String content, Member author) {
        FreedomPostComment freedomPostComment = new FreedomPostComment();
        freedomPostComment.setContent(content);
        freedomPostComment.setCreateDate(LocalDateTime.now());
        freedomPostComment.setFreedomPost(freedomPost);
        freedomPostComment.setAuthor(author);
        this.freedomPostCommentRepository.save(freedomPostComment);

        return freedomPostComment;
    }

    public FreedomPostComment getComment(Long id) {
        return this.freedomPostCommentRepository.findById(id).orElseThrow(
                () -> new DataNotFoundException("해당 댓글을 찾을 수 없습니다.")
        );
    }

    public void delete(FreedomPostComment freedomPostComment) {
        this.freedomPostCommentRepository.delete(freedomPostComment);
    }

    public Page<FreedomPostComment> getList(FreedomPost freedomPost, int commentPage) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));

        Pageable pageable = PageRequest.of(commentPage, 5, Sort.by(sorts));
        return this.freedomPostCommentRepository.findAllByFreedomPost(freedomPost, pageable);
    }
}
