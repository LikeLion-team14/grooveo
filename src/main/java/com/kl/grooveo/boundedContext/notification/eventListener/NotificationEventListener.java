package com.kl.grooveo.boundedContext.notification.eventListener;

import com.kl.grooveo.base.event.*;
import com.kl.grooveo.boundedContext.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class NotificationEventListener {
    private final NotificationService notificationService;


    @EventListener
    @Transactional
    public void listen(EventAfterPostLike event) {
        notificationService.whenAfterPostLike(event.getThumbsUp());
    }

    @EventListener
    @Transactional
    public void listen(EventAfterSoundComment event) {
        notificationService.whenAfterSoundComment(event.getCommentAuthor(), event.getArtist());
    }

    @EventListener
    @Transactional
    public void listen(EventAfterComment event) {
        notificationService.whenAfterComment(event.getCommentAuthor(), event.getPostAuthor());
    }

    @EventListener
    @Transactional
    public void listen(EventAfterFollow event) {
        notificationService.whenAfterFollow(event.getFollow());
    }

    @EventListener
    @Transactional
    public void listen(EventAfterUnFollow event) {
        notificationService.whenAfterUnFollow(event.getFollow());
    }

    @EventListener
    @Transactional
    public void listen(EventAfterSoundLike event) {
        notificationService.whenAfterSoundLike(event.getSoundThumbsUp());
    }
}