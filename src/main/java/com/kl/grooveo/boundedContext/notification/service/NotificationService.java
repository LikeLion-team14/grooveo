package com.kl.grooveo.boundedContext.notification.service;

import com.kl.grooveo.base.exception.DataNotFoundException;
import com.kl.grooveo.boundedContext.community.entity.FreedomPost;
import com.kl.grooveo.boundedContext.community.service.FreedomPostService;
import com.kl.grooveo.boundedContext.follow.entity.Follow;
import com.kl.grooveo.boundedContext.member.entity.Member;
import com.kl.grooveo.boundedContext.notification.entity.Notification;
import com.kl.grooveo.boundedContext.notification.repository.NotificationRepository;
import com.kl.grooveo.boundedContext.thumbsUp.entity.ThumbsUp;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
        Notification notification = Notification
                .builder()
                .fromMember(thumbsUp.getMember()) // 좋아요 한 멤버
                .toMember(thumbsUp.getFreedomPost().getAuthor()) // 좋아요 받은 멤버
                .typeCode("postLike") // "postLike" -> 커뮤니티 게시글 좋아요 알림
                .build();

        notificationRepository.save(notification);

    }

    @Transactional
    public void whenAfterComment(Member fromMember, Member toMember) {
        Notification notification = Notification
                .builder()
                .fromMember(fromMember) // 댓글 단 멤버
                .toMember(toMember) // 게시글 쓴 멤버
                .typeCode("comment") // "postLike" -> 커뮤니티 게시글 댓글 입력 알림
                .build();

        notificationRepository.save(notification);

    }

    @Transactional
    public void whenAfterFollow(Follow follow) {
        Notification notification = Notification
                .builder()
                .fromMember(follow.getFollower())
                .toMember(follow.getFollowing())
                .typeCode("follow")
                .build();

        notificationRepository.save(notification);
    }

    @Transactional
    public boolean getAfterReadNotification(Long notificationId) {
        LocalDateTime localDateTime = LocalDateTime.now();
        Optional<Notification> notification = notificationRepository.findById(notificationId);
        if (notification.isEmpty()) return false;

        if (notification.get().getReadDate() == null) {
            notification.get().setAfterReadNotification(localDateTime);
        }
        return true;
    }

    public boolean countUnreadNotificationsByToMember(Member member) {
        return notificationRepository.countByToMemberAndReadDateIsNull(member) > 0;
    }
}
