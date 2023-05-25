package com.kl.grooveo.boundedContext.member.controller;

import com.kl.grooveo.base.rq.Rq;
import com.kl.grooveo.base.rsData.RsData;
import com.kl.grooveo.boundedContext.member.entity.Member;
import com.kl.grooveo.boundedContext.member.entity.JoinForm;
import com.kl.grooveo.boundedContext.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;
    private final Rq rq;

    @GetMapping("/join")
    public String showJoin() {
        return "/member/join";
    }

    @PostMapping("/join")
    public String join(@Valid JoinForm joinForm) {
        RsData<Member> joinRs = memberService.join(joinForm.getMemberId(), joinForm.getMemberPassword(),
                joinForm.getName(), joinForm.getNickName(), joinForm.getEmail());
        if (!joinForm.getMemberPassword().equals(joinForm.getConfirmPassword())) {
            return rq.historyBack("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
        }

        if (joinRs.isFail()) {
            return rq.historyBack(joinRs);
        }

        return rq.redirectWithMsg("/member/login", joinRs);
    }


    @GetMapping("/login")
    public String login() {
        return "/member/login";
    }
}
