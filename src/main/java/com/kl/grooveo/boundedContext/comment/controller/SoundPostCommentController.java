package com.kl.grooveo.boundedContext.comment.controller;

import org.springframework.data.domain.Page;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;

import com.kl.grooveo.base.rq.Rq;
import com.kl.grooveo.boundedContext.comment.dto.CommentFormDTO;
import com.kl.grooveo.boundedContext.comment.entity.SoundPostComment;
import com.kl.grooveo.boundedContext.comment.service.SoundPostCommentService;
import com.kl.grooveo.boundedContext.library.entity.SoundTrack;
import com.kl.grooveo.boundedContext.library.service.SoundTrackService;
import com.kl.grooveo.boundedContext.member.entity.Member;
import com.kl.grooveo.boundedContext.member.service.MemberService;
import com.kl.grooveo.boundedContext.reply.dto.ReplyFormDTO;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequestMapping("/library/comment")
@RequiredArgsConstructor
@Controller
public class SoundPostCommentController {
	private final SoundTrackService soundTrackService;
	private final SoundPostCommentService soundPostCommentService;
	private final MemberService memberService;
	private final Rq rq;

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/create/{id}")
	public String create(Model model, @PathVariable("id") Long id,
		@RequestParam(value = "commentPage", defaultValue = "0") int commentPage,
		@RequestParam(value = "so", defaultValue = "create") String so,
		@Valid CommentFormDTO commentForm, BindingResult bindingResult, ReplyFormDTO replyForm) {

		SoundTrack soundTrack = soundTrackService.getSoundTrack(id);
		Member member = memberService.findByUsername(rq.getMember().getUsername()).orElseThrow();
		Page<SoundPostComment> commentPaging = getCommentPaging(soundTrack, commentPage, "create");

		if (bindingResult.hasErrors()) {
			model.addAttribute("soundTrack", soundTrack);
			model.addAttribute("commentPaging", commentPaging);
			return "usr/library/soundDetail";
		}

		SoundPostComment soundPostComment = createSoundPostComment(soundTrack, commentForm.getContent(), member);
		commentPaging = getCommentPaging(soundTrack, commentPage, so);

		if (so.equals("recent")) {
			return createRedirectUrl(soundPostComment.getSoundTrack().getId(), 0, so, soundPostComment.getId());
		}

		return createRedirectUrl(soundPostComment.getSoundTrack().getId(), commentPaging.getTotalPages() - 1, so,
			soundPostComment.getId());
	}

	@PreAuthorize("isAuthenticated()")
	@DeleteMapping("/{id}")
	public String delete(@PathVariable("id") Long id) {
		SoundPostComment soundPostComment = soundPostCommentService.getComment(id);

		if (!soundPostComment.getAuthor().getUsername().equals(rq.getMember().getUsername())) {
			throw new AccessDeniedException("삭제권한이 없습니다.");
		}

		soundPostCommentService.delete(soundPostComment);
		return createDetailRedirectUrl(soundPostComment.getSoundTrack().getId());
	}

	private Page<SoundPostComment> getCommentPaging(SoundTrack soundTrack, int commentPage, String so) {
		return soundPostCommentService.getList(soundTrack, commentPage, so);
	}

	private SoundPostComment createSoundPostComment(SoundTrack soundTrack, String content, Member member) {
		return soundPostCommentService.create(soundTrack, content, member);
	}

	private String createRedirectUrl(Long postId, int commentPage, String so, Long commentId) {
		UriComponentsBuilder builder = UriComponentsBuilder.fromPath("/library/soundDetail/{postId}")
			.queryParam("commentPage", commentPage)
			.queryParam("so", so)
			.fragment("comment-" + commentId);

		return "redirect:" + builder.buildAndExpand(postId).toUriString();
	}

	private String createDetailRedirectUrl(Long postId) {
		return "redirect:/library/soundDetail/" + postId;
	}
}
