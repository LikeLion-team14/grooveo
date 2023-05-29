package com.kl.grooveo.boundedContext.member.service;

import com.kl.grooveo.base.email.service.EmailService;
import com.kl.grooveo.base.rsData.RsData;
import com.kl.grooveo.boundedContext.member.entity.Member;
import com.kl.grooveo.boundedContext.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public Optional<Member> findByUsername(String username) {
        return memberRepository.findByUsername(username);
    }

    @Transactional
    public RsData<Member> join(String username, String password, String name, String nickName, String email) {
        return join("GROOVEO", username, password, name, nickName, email);
    }

    private RsData<Member> join(String providerTypeCode, String username, String password, String name, String nickName, String email) {

        if (findByUsername(username).isPresent()) {
            return RsData.of("F-1", "해당 아이디(%s)는 이미 사용중입니다.".formatted(username));
        }

        if (StringUtils.hasText(password)) password = passwordEncoder.encode(password);

        Member member = Member
                .builder()
                .providerTypeCode(providerTypeCode)
                .username(username)
                .password(password)
                .name(name)
                .nickName(nickName)
                .email(email)
                .build();

        memberRepository.save(member);

        if (member.getEmail() != null) {
            emailService.sendRegistrationEmail(member);
        }

        return RsData.of("S-1", "회원가입이 완료되었습니다.", member);
    }

    @Transactional
    public RsData<Member> whenSocialLogin(String providerTypeCode, String username) {
        Optional<Member> opMember = findByUsername(username);

        if (opMember.isPresent()) {
            return RsData.of("S-2", "로그인 되었습니다.", opMember.get());
        }

        return join(providerTypeCode, username, "", "", null, null);
    }

    public RsData findUsername(String email) {
        Optional<Member> opActor = memberRepository.findByEmail(email);

        if (opActor.isEmpty()) {
            return RsData.of("F-1", "등록된 아이디를 찾을 수 없습니다.");
        }

        emailService.sendUsername(opActor.get());

        return RsData.of("S-1", "등록하신 이메일로 아이디를 발송했습니다.");
    }

}
