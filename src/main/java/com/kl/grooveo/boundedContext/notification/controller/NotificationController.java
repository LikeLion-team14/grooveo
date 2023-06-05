package com.kl.grooveo.boundedContext.notification.controller;

import com.kl.grooveo.base.rq.Rq;
import com.kl.grooveo.boundedContext.notification.entity.Notification;
import com.kl.grooveo.boundedContext.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/usr/notification")
@RequiredArgsConstructor
public class NotificationController {
    private final Rq rq;
    private final NotificationService notificationService;

    @GetMapping("/list")
    @PreAuthorize("isAuthenticated()")
    public String showList(Model model) {

        List<Notification> notifications = notificationService.findByToMember(rq.getMember());

        // 알림을 확인
        notificationService.getAfterReadNotification(notifications);

        model.addAttribute("notifications", notifications);

        return "usr/notification/list";
    }
}
