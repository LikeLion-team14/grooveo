package com.kl.grooveo.boundedContext.follow.controller;

import com.kl.grooveo.base.rq.Rq;
import com.kl.grooveo.boundedContext.follow.service.FollowService;
import com.kl.grooveo.boundedContext.member.entity.Member;
import com.kl.grooveo.boundedContext.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class FollowController {

    private final FollowService followService;
    private final MemberService memberService;
    private final Rq rq;

    @PostMapping("/follow")
    @ResponseBody
    public String follow(@RequestParam String username) throws Exception {
        Member actor = rq.getMember();
        Member followingUser = memberService.findByUsername(username).orElseThrow();

        followService.following(actor,followingUser);

        return "follow";
    }

    @PostMapping("/unFollow")
    @ResponseBody
    public String unFollow(@RequestParam String username) {
        Member actor = rq.getMember();
        Member followingUser = memberService.findByUsername(username).orElseThrow();

        followService.unFollowing(actor,followingUser);

        return "unFollow";
    }
}
