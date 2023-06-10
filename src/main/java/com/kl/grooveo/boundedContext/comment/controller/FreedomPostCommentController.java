package com.kl.grooveo.boundedContext.comment.controller;

import com.kl.grooveo.base.rq.Rq;
import com.kl.grooveo.boundedContext.comment.entity.FreedomPostComment;
import com.kl.grooveo.boundedContext.comment.service.FreedomPostCommentService;
import com.kl.grooveo.boundedContext.community.entity.FreedomPost;
import com.kl.grooveo.boundedContext.community.service.FreedomPostService;
import com.kl.grooveo.boundedContext.form.CommentForm;
import com.kl.grooveo.boundedContext.form.ReplyForm;
import com.kl.grooveo.boundedContext.member.entity.Member;
import com.kl.grooveo.boundedContext.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RequestMapping("/comment")
@RequiredArgsConstructor
@Controller
public class FreedomPostCommentController {
    private final FreedomPostService freedomPostService;
    private final FreedomPostCommentService freedomPostCommentService;
    private final MemberService memberService;
    private final Rq rq;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create/{id}")
    public String create(Model model, @PathVariable("id") Long id,
                         @RequestParam(value = "commentPage", defaultValue = "0") int commentPage,
                         @Valid CommentForm commentForm, BindingResult bindingResult, ReplyForm replyForm) {

        FreedomPost freedomPost = freedomPostService.getFreedomPost(id);
        Member member = memberService.findByUsername(rq.getMember().getUsername()).orElseThrow();

        Page<FreedomPostComment> commentPaging = this.freedomPostCommentService.getList(freedomPost, commentPage, "create");

        if (bindingResult.hasErrors()) {
            model.addAttribute("freedomPost", freedomPost);
            model.addAttribute("commentPaging", commentPaging);
            return "usr/community/freedomPost/detail";
        }

        FreedomPostComment freedomPostComment = freedomPostCommentService.create(freedomPost, commentForm.getContent(), member);
        return String.format("redirect:/community/freedomPost/detail/%s#comment-%s",
                freedomPostComment.getFreedomPost().getId(), freedomPostComment.getId());
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") Long id) {
        FreedomPostComment freedomPostComment = this.freedomPostCommentService.getComment(id);

        if (!freedomPostComment.getAuthor().getUsername().equals(rq.getMember().getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }

        this.freedomPostCommentService.delete(freedomPostComment);
        return String.format("redirect:/community/freedomPost/detail/%s", freedomPostComment.getFreedomPost().getId());
    }
}
