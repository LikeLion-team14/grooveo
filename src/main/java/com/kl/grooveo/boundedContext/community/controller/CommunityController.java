package com.kl.grooveo.boundedContext.community.controller;

import com.kl.grooveo.boundedContext.community.entity.Community;
import com.kl.grooveo.boundedContext.community.service.CommunityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
}
