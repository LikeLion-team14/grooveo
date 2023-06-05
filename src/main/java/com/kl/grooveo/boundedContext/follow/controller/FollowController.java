package com.kl.grooveo.boundedContext.follow.controller;

import com.kl.grooveo.base.rq.Rq;
import com.kl.grooveo.boundedContext.follow.entity.Follow;
import com.kl.grooveo.boundedContext.follow.service.FollowService;
import com.kl.grooveo.boundedContext.member.entity.Member;
import com.kl.grooveo.boundedContext.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;
    private final MemberService memberService;
    private final Rq rq;

    @PostMapping("/follow")
    @ResponseBody
    public ResponseEntity<String> follow(@RequestBody Map<String, String> requestData) {
        String username = requestData.get("username");

        // 현재 로그인한 사용자의 정보 가져오기
        Member follower = rq.getMember();

        // 선택한 사용자의 정보 가져오기
        Member following = memberService.findByUsername(username).orElse(null);

        if (following == null) {
            return ResponseEntity.badRequest().body("User not found");
        }

        Optional<Follow> follow = followService.findByFollowerAndFollowing(follower, following);
        if (follow.isPresent()) {
            // 이미 팔로우 중인 경우 언팔로우 수행
            try {
                followService.unFollowing(follower, following);
                return ResponseEntity.ok("Unfollowing success!");
            } catch (Exception e) {
                return ResponseEntity.badRequest().body("Unfollowing failed: " + e.getMessage());
            }
        } else {
            // 아직 팔로우하지 않은 경우 팔로우 수행
            try {
                followService.following(follower, following);
                return ResponseEntity.ok("Following success!");
            } catch (Exception e) {
                return ResponseEntity.badRequest().body("Following failed: " + e.getMessage());
            }
        }
    }
}
