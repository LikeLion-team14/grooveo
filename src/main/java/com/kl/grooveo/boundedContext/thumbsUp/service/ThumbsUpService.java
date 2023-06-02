package com.kl.grooveo.boundedContext.thumbsUp.service;

import com.kl.grooveo.boundedContext.thumbsUp.entity.ThumbsUp;
import com.kl.grooveo.boundedContext.thumbsUp.entity.ThumbsUp_summary;
import com.kl.grooveo.boundedContext.thumbsUp.repository.ThumbsUpRepository;
import com.kl.grooveo.boundedContext.thumbsUp.repository.ThumbsUp_summaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ThumbsUpService {
    private final ThumbsUp_summaryRepository thumbsUpSummaryRepository;
    private final ThumbsUpRepository thumbsUpRepository;

    public void likePost(Long postId, Long memberId) {
        ThumbsUp thumbsUp = new ThumbsUp();
        thumbsUp.setPostId(postId);
        thumbsUp.setMemberId(memberId);
        thumbsUpRepository.save(thumbsUp);

        if (!thumbsUpSummaryRepository.existsByPostId(postId)) {
            ThumbsUp_summary thumbsUpSummary= new ThumbsUp_summary();
            thumbsUpSummary.setPostId(postId);
            thumbsUpSummary.setLikeCount(0);
            thumbsUpSummaryRepository.save(thumbsUpSummary);
        }
    }

    public void unlikePost(Long postId, Long memberId) {
        Optional<ThumbsUp> thumbsUpOptional = thumbsUpRepository.findByPostIdAndMemberId(postId, memberId);
        thumbsUpOptional.ifPresent(thumbsUpRepository::delete);

        if (!thumbsUpSummaryRepository.existsByPostId(postId)) {
            ThumbsUp_summary thumbsUpSummary= new ThumbsUp_summary();
            thumbsUpSummary.setPostId(postId);
            thumbsUpSummary.setLikeCount(0);
            thumbsUpSummaryRepository.save(thumbsUpSummary);
        }
    }

    public boolean isLikedByMember(Long postId, Long memberId) {
        return thumbsUpRepository.existsByPostIdAndMemberId(postId, memberId);
    }
}