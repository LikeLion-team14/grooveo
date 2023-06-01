package com.kl.grooveo.boundedContext.member.controller;

import com.kl.grooveo.base.email.service.EmailService;
import com.kl.grooveo.base.rq.Rq;
import com.kl.grooveo.base.rsData.RsData;
import com.kl.grooveo.boundedContext.member.entity.Member;
import com.kl.grooveo.boundedContext.member.form.FindPasswordForm;
import com.kl.grooveo.boundedContext.member.form.FindUsernameForm;
import com.kl.grooveo.boundedContext.member.form.JoinForm;
import com.kl.grooveo.boundedContext.member.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
@RequestMapping("/usr/member")
public class MemberController {

    private final MemberService memberService;
    private final EmailService emailService;
    private final Rq rq;

    @GetMapping("/join")
    public String showJoin() {
        return "usr/member/join";
    }

    @PostMapping("/join")
    public String join(@Valid JoinForm joinForm) {
        RsData<Member> joinRs = memberService.join(joinForm.getUsername(), joinForm.getPassword(),
                joinForm.getName(), joinForm.getNickName(), joinForm.getEmail());

        if (joinRs.isFail()) {
            return rq.historyBack(joinRs);
        }

        return rq.redirectWithMsg("/usr/member/login", joinRs);
    }

    @GetMapping("/login")
    public String login() {
        return "usr/member/login";
    }

    @GetMapping("/findUsername")
    public String showFindId() {
        return "usr/member/findUsername";
    }

    @PostMapping("/findUsername")
    public String findId(@Valid FindUsernameForm findUsernameForm) {
        RsData findIdRs = memberService.findUsername(findUsernameForm.getEmail());

        if (findIdRs.isFail()) {
            return rq.historyBack(findIdRs);
        }

        return rq.redirectWithMsg("/usr/member/login", findIdRs);
    }

    @GetMapping("/findPassword")
    public String showFindPassword() {
        return "usr/member/findPassword";
    }

    @PostMapping("/findPassword")
    public String findPassword(@Valid FindPasswordForm findPasswordForm) {
        RsData findPasswordRs = memberService.findUserPassword(findPasswordForm.getUsername(), findPasswordForm.getEmail());

        if (findPasswordRs.isFail()) {
            return rq.historyBack(findPasswordRs);
        }

        return rq.redirectWithMsg("/usr/member/login", findPasswordRs);
    }

    @PostMapping("/sendCode")
    @ResponseBody
    public String sendVerificationCode(HttpServletRequest request, String userEmail) {
        HttpSession session = request.getSession();

        emailService.sendVerificationCode(session, userEmail);

        return "";
    }

}