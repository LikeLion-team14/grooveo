package com.kl.grooveo.boundedContext.thumbsUp.service;

import com.kl.grooveo.boundedContext.response.ThumbsUpDTO;
import com.kl.grooveo.boundedContext.thumbsUp.entity.ThumbsUp_summary;
import com.kl.grooveo.boundedContext.thumbsUp.repository.ThumbsUp_summaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ThumbsUp_summaryService {
    private final ThumbsUp_summaryRepository thumbsUpSummaryRepository;

    @Transactional
    public void updateLikeCount(Long postId, int num) {
        ThumbsUp_summary thumbsUpSummary = thumbsUpSummaryRepository.findByPostId(postId);
        if (thumbsUpSummary != null) {
            thumbsUpSummary.setLikeCount(thumbsUpSummary.getLikeCount() + num);
            thumbsUpSummaryRepository.save(thumbsUpSummary);
        }
    }

    @Transactional
    public ThumbsUpDTO getLikeCount(Long postId, int plusNum) {
        ThumbsUp_summary thumbsUpSummary = thumbsUpSummaryRepository.findByPostId(postId);
        if (thumbsUpSummary == null) {
            return new ThumbsUpDTO("error", -1);
        }

        String result = (plusNum == 1) ? "unliked" : "liked";
        return new ThumbsUpDTO(result, thumbsUpSummary.getLikeCount());
    }

    public int getLikeCount(Long postId) {
        ThumbsUp_summary thumbsUpSummary = thumbsUpSummaryRepository.findByPostId(postId);
        if (thumbsUpSummary == null) {
            return -1;
        }

        return thumbsUpSummary.getLikeCount();
    }
}
