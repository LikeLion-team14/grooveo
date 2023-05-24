package com.kl.grooveo.boundedContext.community.controller;

import com.kl.grooveo.boundedContext.community.entity.Community;
import com.kl.grooveo.boundedContext.community.form.CommunityForm;
import com.kl.grooveo.boundedContext.community.service.CommunityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
}
