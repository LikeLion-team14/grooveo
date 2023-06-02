package com.kl.grooveo.boundedContext.notification.service;

import com.kl.grooveo.boundedContext.community.entity.FreedomPost;
import com.kl.grooveo.boundedContext.community.service.FreedomPostService;
import com.kl.grooveo.boundedContext.member.entity.Member;
import com.kl.grooveo.boundedContext.notification.entity.Notification;
import com.kl.grooveo.boundedContext.notification.repository.NotificationRepository;
import com.kl.grooveo.boundedContext.thumbsUp.entity.ThumbsUp;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final FreedomPostService freedomPostService;

    public List<Notification> findByToMember(Member toMember) {
        return notificationRepository.findByToMemberOrderByCreateDateDesc(toMember);
    }

    @Transactional
    public void whenAfterPostLike(ThumbsUp thumbsUp) {
        FreedomPost freedomPost = freedomPostService.getFreedomPost(thumbsUp.getPostId());
        Notification notification = Notification
                .builder()
                .fromMember(thumbsUp.getMember()) // 좋아요 한 멤버
                .toMember(freedomPost.getAuthor()) // 좋아요 받은 멤버
                .typeCode("postLike") // "postLike" -> 커뮤니티 게시글 좋아요 알림
                .build();

        notificationRepository.save(notification);

    }

    @Transactional
    public void whenAfterFollow() {
//        Notification notification = Notification
//                .builder()
//                .fromMember()
//                .toMember()
//                .typeCode("follow")
//                .build();
//
//        notificationRepository.save(notification);
    }

    @Transactional
    public void getAfterReadNotification(List<Notification> notificationList) {
        LocalDateTime localDateTime = LocalDateTime.now();

        for (Notification notification : notificationList) {
            // readDate 가 null 이면 현재 날짜 입력
            if (notification.getReadDate() == null){
                notification.setAfterReadNotification(localDateTime);
            }
        }
    }

    public boolean countUnreadNotificationsByToMember(Member member) {
        return notificationRepository.countByToMemberAndReadDateIsNull(member) > 0;
    }
}
