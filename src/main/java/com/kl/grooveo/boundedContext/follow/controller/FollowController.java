package com.kl.grooveo.boundedContext.follow.controller;

import com.kl.grooveo.base.rq.Rq;
import com.kl.grooveo.base.rsData.RsData;
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
        if (rq.isLogout()) {
            return rq.redirectWithMsg("/usr/member/login", "로그인이 필요합니다.");
        }

        Member actor = rq.getMember();
        Member followingUser = memberService.findByUsername(username).orElseThrow();

        RsData followRsdata = followService.following(actor, followingUser);

        if (followRsdata.isFail()) {
            return rq.historyBack(followRsdata);
        }

        return "follow";
    }

    @PostMapping("/unFollow")
    @ResponseBody
    public String unFollow(@RequestParam String username) {
        if (rq.isLogout()) {
            return rq.redirectWithMsg("/usr/member/login", "로그인이 필요합니다.");
        }

        Member actor = rq.getMember();
        Member followingUser = memberService.findByUsername(username).orElseThrow();

        RsData unFollowRsdata = followService.unFollowing(actor, followingUser);

        if (unFollowRsdata.isFail()) {
            return rq.historyBack(unFollowRsdata);
        }
        return "unFollow";
    }

    @PostMapping("/deleteFollower")
    @ResponseBody
    public String deleteFollow(@RequestParam String username) {
        if (rq.isLogout()) {
            return rq.redirectWithMsg("/usr/member/login", "로그인이 필요합니다.");
        }

        Member actor = rq.getMember();
        Member followingUser = memberService.findByUsername(username).orElseThrow();

        RsData unFollowRsdata = followService.unFollowing(followingUser, actor);

        if (unFollowRsdata.isFail()) {
            return rq.historyBack(unFollowRsdata);
        }
        return "deleteFollower";
    }

    @GetMapping("/updateFollowerCount")
    public int updateFollowerCount() {
        Member actor = rq.getMember();

        return actor.getFollowingPeople().size();
    }
}