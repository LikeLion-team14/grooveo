package com.kl.grooveo.boundedContext.community.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import com.kl.grooveo.base.rq.Rq;
import com.kl.grooveo.boundedContext.comment.dto.CommentFormDTO;
import com.kl.grooveo.boundedContext.comment.entity.FreedomPostComment;
import com.kl.grooveo.boundedContext.comment.service.FreedomPostCommentService;
import com.kl.grooveo.boundedContext.community.dto.FreedomPostFormDTO;
import com.kl.grooveo.boundedContext.community.entity.Category;
import com.kl.grooveo.boundedContext.community.entity.FreedomPost;
import com.kl.grooveo.boundedContext.community.service.FreedomPostService;
import com.kl.grooveo.boundedContext.reply.dto.ReplyFormDTO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequestMapping("/community/freedomPost")
@RequiredArgsConstructor
@Controller
public class FreedomPostController {
	private final FreedomPostService freedomPostService;
	private final FreedomPostCommentService freedomPostCommentService;
	private final Rq rq;
	static int boardTypeCode;

	@GetMapping("/{boardType}/list")
	@PreAuthorize("isAuthenticated()")
	public String showList(Model model, @PathVariable("boardType") Integer boardType,
		@RequestParam(value = "page", defaultValue = "0") int page,
		@RequestParam(value = "kw", defaultValue = "") String kw,
		@RequestParam(value = "category", required = false, defaultValue = "all") String selectedCategoryCode) {
		Page<FreedomPost> paging = this.freedomPostService.getList(boardType, selectedCategoryCode, kw, page);
		model.addAttribute("boardType", boardType);
		model.addAttribute("paging", paging);
		model.addAttribute("kw", kw);
		model.addAttribute("categories", Category.values());
		model.addAttribute("selectedCategoryCode", selectedCategoryCode);
		boardTypeCode = boardType;

		return "usr/community/freedomPost/list";
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping(value = "/detail/{id}")
	public String showMoreDetail(Model model, @PathVariable("id") Long id,
		@RequestParam(value = "so", defaultValue = "create") String so,
		@RequestParam(value = "commentPage", defaultValue = "0") int commentPage, CommentFormDTO commentFormDTO,
		ReplyFormDTO replyFormDTO,
		HttpServletRequest request, HttpServletResponse response) {

		freedomPostService.updateView(request, response, id);

		FreedomPost freedomPost = this.freedomPostService.getFreedomPost(id);

		Page<FreedomPostComment> commentPaging = this.freedomPostCommentService.getList(freedomPost, commentPage, so);

		model.addAttribute("commentPaging", commentPaging);
		model.addAttribute("freedomPost", freedomPost);
		model.addAttribute("so", so);

		return "usr/community/freedomPost/detail";
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/create")
	public String freedomPostCreate(FreedomPostFormDTO freedomPostFormDTO) {
		return "usr/community/freedomPost/form";
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/create")
	public String freedomPostCreate(@Valid FreedomPostFormDTO freedomPostFormDTO,
		BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return "usr/community/freedomPost/form";
		}

		this.freedomPostService.create(boardTypeCode, freedomPostFormDTO.getTitle(), freedomPostFormDTO.getCategory(),
			freedomPostFormDTO.getContent(), rq.getMember());
		return String.format("redirect:/community/freedomPost/%d/list", boardTypeCode);
	}

	@PreAuthorize("isAuthenticated()")
	@DeleteMapping("/{id}")
	public String freedomPostDelete(@PathVariable("id") Long id) {
		FreedomPost freedomPost = this.freedomPostService.getFreedomPost(id);

		if (!freedomPost.getAuthor().getUsername().equals(rq.getMember().getUsername())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
		}

		this.freedomPostService.delete(freedomPost);
		return String.format("redirect:/community/freedomPost/%d/list", boardTypeCode);
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/modify/{id}")
	public String freedomPostModify(FreedomPostFormDTO freedomPostForm, @PathVariable("id") Long id) {
		FreedomPost freedomPost = this.freedomPostService.getFreedomPost(id);

		if (!freedomPost.getAuthor().getUsername().equals(rq.getMember().getUsername())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
		}

		freedomPostForm.setTitle(freedomPost.getTitle());
		freedomPostForm.setCategory(freedomPost.getCategory());
		freedomPostForm.setContent(freedomPost.getContent());
		return "usr/community/freedomPost/form";
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/modify/{id}")
	public String freedomPostModify(@Valid FreedomPostFormDTO freedomPostForm, BindingResult bindingResult,
		@PathVariable("id") Long id) {
		if (bindingResult.hasErrors()) {
			return "usr/community/freedomPost/form";
		}
		FreedomPost freedomPost = this.freedomPostService.getFreedomPost(id);

		if (!freedomPost.getAuthor().getUsername().equals(rq.getMember().getUsername())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
		}

		this.freedomPostService.modify(freedomPost, freedomPostForm.getTitle(), freedomPostForm.getCategory(),
			freedomPostForm.getContent());
		return String.format("redirect:/community/freedomPost/detail/%s", id);
	}

	@GetMapping("/getView")
	@ResponseBody
	public String getViewCnt(@RequestParam("postId") Long postId) {
		return String.valueOf(freedomPostService.getViewCnt(postId));  // 조회수 String 처리
	}
}
