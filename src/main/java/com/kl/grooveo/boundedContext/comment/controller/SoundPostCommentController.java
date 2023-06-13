package com.kl.grooveo.boundedContext.comment.controller;

import com.kl.grooveo.base.rq.Rq;
import com.kl.grooveo.boundedContext.comment.entity.FreedomPostComment;
import com.kl.grooveo.boundedContext.comment.entity.SoundPostComment;
import com.kl.grooveo.boundedContext.comment.service.SoundPostCommentService;
import com.kl.grooveo.boundedContext.community.entity.FreedomPost;
import com.kl.grooveo.boundedContext.form.CommentForm;
import com.kl.grooveo.boundedContext.library.entity.FileInfo;
import com.kl.grooveo.boundedContext.library.service.FileInfoService;
import com.kl.grooveo.boundedContext.member.entity.Member;
import com.kl.grooveo.boundedContext.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.UriComponentsBuilder;

@RequestMapping("/answer")
@RequiredArgsConstructor
@Controller
public class SoundPostCommentController {
    private final FileInfoService fileInfoService;
    private final SoundPostCommentService soundPostCommentService;
    private final MemberService memberService;
    private final Rq rq;

    @PostMapping("/create/{id}")
    public String create(Model model, @PathVariable("id") Long id,
                               @Valid CommentForm commentForm, BindingResult bindingResult) {

        FileInfo fileInfo = fileInfoService.findById(id);
        Member member = memberService.findByUsername(rq.getMember().getUsername()).orElseThrow();

        if (bindingResult.hasErrors()) {
            model.addAttribute("fileInfo", fileInfo);
            return "usr/library/soundDetail";
        }

        SoundPostComment soundPostComment = soundPostCommentService.create(fileInfo, commentForm.getContent(), member);
        return String.format("redirect:/library/soundDetail/%s#comment_%s",
                soundPostComment.getFileInfo().getId(), soundPostComment.getId());
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") Long id) {
        SoundPostComment soundPostComment = this.soundPostCommentService.getComment(id);

        this.soundPostCommentService.delete(soundPostComment);
        return String.format("redirect:/library/soundDetail/%s", soundPostComment.getFileInfo().getId());

        //return createDetailRedirectUrl(soundPostComment.getFileInfo().getId());
    }

    private Page<SoundPostComment> getCommentPaging(FileInfo fileInfo, int commentPage, String so) {
        return soundPostCommentService.getList(fileInfo, commentPage, so);
    }

    private SoundPostComment createSoundPostComment(FileInfo fileInfo, String content, Member member) {
        return soundPostCommentService.create(fileInfo, content, member);
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
