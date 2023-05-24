package com.kl.grooveo.boundedContext.community.controller;

import com.kl.grooveo.boundedContext.community.entity.Community;
import com.kl.grooveo.boundedContext.community.form.CommunityForm;
import com.kl.grooveo.boundedContext.community.service.CommunityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

@RequestMapping("/community")
@RequiredArgsConstructor
@Controller
public class CommunityController {
    private final CommunityService communityService;

    @GetMapping("/list")
    public String list(Model model) {
        List<Community> communityList = this.communityService.getList();
        model.addAttribute("communityList", communityList);
        return "usr/community/list";
    }

    @GetMapping(value = "/detail/{id}")
    public String detail(Model model, @PathVariable("id") Long id) throws Exception {
        Community community = this.communityService.getMoreInformation(id);
        model.addAttribute("community", community);
        return "usr/community/detail";
    }

    @GetMapping("/create")
    public String questionCreate(Model model, CommunityForm communityForm) {
        return "usr/community/form";
    }

    @PostMapping("/create")
    public String questionCreate(Model model, @Valid CommunityForm communityForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "usr/community/form";
        }
        String author = "test_user1";

        this.communityService.create(communityForm.getTitle(), communityForm.getCategory(), communityForm.getContent(), author);
        return "redirect:/community/list";
    }

    @GetMapping("/delete/{id}")
    public String questionDelete(Principal principal, @PathVariable("id") Long id) throws Exception {
        Community community = this.communityService.getCommunity(id);
//        if (!community.getAuthor().getUsername().equals(principal.getName())) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
//        }
        this.communityService.delete(community);
        return "redirect:/community";
    }

    @GetMapping("/modify/{id}")
    public String questionModify(CommunityForm communityForm, @PathVariable("id") Long id, Principal principal) throws Exception {
        Community community = this.communityService.getCommunity(id);
//        if(!community.getAuthor().getUsername().equals(principal.getName())) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
//        }
        communityForm.setTitle(community.getTitle());
        communityForm.setCategory(community.getCategory());
        communityForm.setContent(community.getContent());
        return "usr/community/form";
    }

    @PostMapping("/modify/{id}")
    public String questionModify(@Valid CommunityForm communityForm, BindingResult bindingResult,
                                 Principal principal, @PathVariable("id") Long id) throws Exception {
        if (bindingResult.hasErrors()) {
            return "usr/community/form";
        }
        Community community = this.communityService.getCommunity(id);
//        if (!community.getAuthor().getUsername().equals(principal.getName())) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
//        }
        String user = "test_user1";
        this.communityService.modify(community, communityForm.getTitle(), communityForm.getCategory(), communityForm.getContent(), user);
        return String.format("redirect:/community/detail/%s", id);
    }
}
