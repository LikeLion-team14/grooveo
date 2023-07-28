package com.kl.grooveo.base.initData;

import java.time.LocalDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

import com.kl.grooveo.boundedContext.comment.service.FreedomPostCommentService;
import com.kl.grooveo.boundedContext.community.entity.FreedomPost;
import com.kl.grooveo.boundedContext.community.service.FreedomPostService;
import com.kl.grooveo.boundedContext.follow.service.FollowService;
import com.kl.grooveo.boundedContext.library.entity.FileInfo;
import com.kl.grooveo.boundedContext.library.service.FileInfoService;
import com.kl.grooveo.boundedContext.member.entity.Member;
import com.kl.grooveo.boundedContext.member.service.MemberService;
import com.kl.grooveo.boundedContext.notification.service.NotificationService;
import com.kl.grooveo.boundedContext.thumbsUp.service.SoundThumbsUpService;
import com.kl.grooveo.boundedContext.thumbsUp.service.SoundThumbsUp_summaryService;

@Configuration
@Profile({"dev"})
public class NotProd {

	@Bean
	CommandLineRunner initData(
		MemberService memberService,
		FreedomPostService freedomPostService,
		FreedomPostCommentService freedomPostCommentService,
		NotificationService notificationService,
		FollowService followService,
		FileInfoService fileInfoService,
		SoundThumbsUpService soundThumbsUpService,
		SoundThumbsUp_summaryService soundThumbsUpSummaryService
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

				followService.following(memberUser5, memberUser1);

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

				for (int i = 0; i < 11; i++) {
					FileInfo fileInfo = FileInfo.builder()
						.title("제목" + i)
						.artist(memberUser1)
						.albumCoverUrl(
							"https://grooveobucket.s3.ap-northeast-2.amazonaws.com/albumCover/free-icon-user-5264565.png")
						.createDate(LocalDateTime.now())
						.description("설명" + i)
						.soundUrl("https://grooveobucket.s3.ap-northeast-2.amazonaws.com/sound/testSound.m4a")
						.build();
					fileInfoService.saveFileInfo(fileInfo);
				}

				FileInfo fileInfo = FileInfo.builder()
					.title("Five")
					.artist(memberUser3)
					.albumCoverUrl("//cdn.atrera.com/images/cover_yz2mak.jpg")
					.description("Marcel Pequel")
					.createDate(LocalDateTime.now())
					.soundUrl("http://cdn.atrera.com/audio/Marcel_Pequel_-_05_-_Five.mp3")
					.build();
				fileInfoService.saveFileInfo(fileInfo);
				FileInfo fileInfo2 = FileInfo.builder()
					.title("Six")
					.artist(memberUser3)
					.albumCoverUrl("//cdn.atrera.com/images/cover_yz2mak.jpg")
					.description("Marcel Pequel")
					.createDate(LocalDateTime.now())
					.soundUrl("http://cdn.atrera.com/audio/Marcel_Pequel_-_06_-_Six.mp3")
					.build();
				fileInfoService.saveFileInfo(fileInfo2);
				FileInfo fileInfo3 = FileInfo.builder()
					.title("Seven")
					.artist(memberUser3)
					.albumCoverUrl("//cdn.atrera.com/images/cover_yz2mak.jpg")
					.description("Marcel Pequel")
					.createDate(LocalDateTime.now())
					.soundUrl("http://cdn.atrera.com/audio/Marcel_Pequel_-_07_-_Seven.mp3")
					.build();
				fileInfoService.saveFileInfo(fileInfo3);

				soundThumbsUpService.likePost(1L, memberUser2.getId());
				soundThumbsUpService.likePost(2L, memberUser2.getId());
				soundThumbsUpService.likePost(3L, memberUser2.getId());
				soundThumbsUpService.likePost(4L, memberUser2.getId());
				soundThumbsUpService.likePost(5L, memberUser2.getId());
				soundThumbsUpService.likePost(6L, memberUser2.getId());
				soundThumbsUpService.likePost(7L, memberUser2.getId());
				soundThumbsUpService.likePost(12L, memberUser2.getId());
				soundThumbsUpService.likePost(13L, memberUser3.getId());
				soundThumbsUpService.likePost(14L, memberUser3.getId());

				soundThumbsUpSummaryService.updateLikeCount(1L, 17);
				soundThumbsUpSummaryService.updateLikeCount(2L, 16);
				soundThumbsUpSummaryService.updateLikeCount(3L, 15);
				soundThumbsUpSummaryService.updateLikeCount(4L, 13);
				soundThumbsUpSummaryService.updateLikeCount(5L, 12);
				soundThumbsUpSummaryService.updateLikeCount(6L, 1);
				soundThumbsUpSummaryService.updateLikeCount(7L, 50);
				soundThumbsUpSummaryService.updateLikeCount(12L, 20);
				soundThumbsUpSummaryService.updateLikeCount(13L, 100);
				soundThumbsUpSummaryService.updateLikeCount(14L, 10);
			}
		};
	}
}