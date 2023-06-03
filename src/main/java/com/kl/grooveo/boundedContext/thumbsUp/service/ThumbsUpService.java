package com.kl.grooveo.boundedContext.thumbsUp.service;

import com.kl.grooveo.base.event.EventAfterComment;
import com.kl.grooveo.base.event.EventAfterPostLike;
import com.kl.grooveo.boundedContext.member.entity.Member;
import com.kl.grooveo.boundedContext.member.service.MemberService;
import com.kl.grooveo.boundedContext.thumbsUp.entity.ThumbsUp;
import com.kl.grooveo.boundedContext.thumbsUp.entity.ThumbsUp_summary;
import com.kl.grooveo.boundedContext.thumbsUp.repository.ThumbsUpRepository;
import com.kl.grooveo.boundedContext.thumbsUp.repository.ThumbsUp_summaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.Flow;

@RequiredArgsConstructor
@Service
public class ThumbsUpService {
    private final ThumbsUp_summaryRepository thumbsUpSummaryRepository;
    private final ThumbsUpRepository thumbsUpRepository;
    private final MemberService memberService;
    private final ApplicationEventPublisher publisher;

    public void likePost(Long postId, Long memberId) {
        Optional<Member> member =  memberService.findById(memberId);
        if (member.isEmpty()) return;

        ThumbsUp thumbsUp = new ThumbsUp();
        thumbsUp.setPostId(postId);
        thumbsUp.setMember(member.get());
        thumbsUpRepository.save(thumbsUp);

        publisher.publishEvent(new EventAfterPostLike(this, thumbsUp));

        if (!thumbsUpSummaryRepository.existsByPostId(postId)) {
            ThumbsUp_summary thumbsUpSummary= new ThumbsUp_summary();
            thumbsUpSummary.setPostId(postId);
            thumbsUpSummary.setLikeCount(0);
            thumbsUpSummaryRepository.save(thumbsUpSummary);
        }
    }

    public void unlikePost(Long postId, Long memberId) {
        Optional<Member> member =  memberService.findById(memberId);
        if (member.isEmpty()) return;
        Optional<ThumbsUp> thumbsUpOptional = thumbsUpRepository.findByPostIdAndMember(postId, member.get());
        thumbsUpOptional.ifPresent(thumbsUpRepository::delete);

        if (!thumbsUpSummaryRepository.existsByPostId(postId)) {
            ThumbsUp_summary thumbsUpSummary= new ThumbsUp_summary();
            thumbsUpSummary.setPostId(postId);
            thumbsUpSummary.setLikeCount(0);
            thumbsUpSummaryRepository.save(thumbsUpSummary);
        }
    }

    public boolean isLikedByMember(Long postId, Member member) {
        return thumbsUpRepository.existsByPostIdAndMember(postId, member);
    }
}