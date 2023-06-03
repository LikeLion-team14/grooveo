package com.kl.grooveo.base.initData;

import com.kl.grooveo.boundedContext.comment.service.FreedomPostCommentService;
import com.kl.grooveo.boundedContext.community.entity.FreedomPost;
import com.kl.grooveo.boundedContext.community.service.FreedomPostService;
import com.kl.grooveo.boundedContext.member.entity.Member;
import com.kl.grooveo.boundedContext.member.service.MemberService;
import com.kl.grooveo.boundedContext.notification.service.NotificationService;
import com.kl.grooveo.boundedContext.thumbsUp.entity.ThumbsUp;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@Profile({"dev"})
public class NotProd {

    @Bean
    CommandLineRunner initData(
            MemberService memberService,
            FreedomPostService freedomPostService,
            FreedomPostCommentService freedomPostCommentService,
            NotificationService notificationService
    ) {
        return new CommandLineRunner() {
            @Override
            @Transactional
            public void run(String... args) throws Exception {
                Member memberUser1 = memberService.join("user1", "1234", "유저1", "유저1입니다", null).getData();
                Member memberUser2 = memberService.join("user2", "1234", "유저2", "유저2입니다", null).getData();
                Member memberUser3 = memberService.join("user3", "1234", "유저3", "유저3입니다", null).getData();
                Member memberUser4 = memberService.join("user4", "1234", "유저4", "유저4입니다", null).getData();
                Member memberUser5 = memberService.join("user5", "1234", "유저5", "유저5입니다", null).getData();


                for (int i = 0; i < 100; i++) {
                    freedomPostService.create(1, "국외 게시판 제목" + i, "c2", "내용", memberUser1);
                    freedomPostService.create(2, "국내 게시판 제목" + i, "c4", "내용", memberUser1);
                }

                FreedomPost freedomPost = freedomPostService.getFreedomPost(199L);
                for (int i = 0; i < 10; i++) {
                    freedomPostCommentService.create(freedomPost, "댓글 내용" + i, memberUser2);
                }

//                notificationService.whenAfterPostLike(new ThumbsUp(199L, memberUser2));
//                notificationService.whenAfterPostLike(new ThumbsUp(199L, memberUser3));
//                notificationService.whenAfterPostLike(new ThumbsUp(199L, memberUser4));
//                notificationService.whenAfterPostLike(new ThumbsUp(199L, memberUser5));


            }
        };
    }
}