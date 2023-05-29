package com.kl.grooveo.base.email.service;

import com.kl.grooveo.boundedContext.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;

    public void sendRegistrationEmail(Member member) {

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

        simpleMailMessage.setTo(member.getEmail());

        simpleMailMessage.setSubject("회원가입을 축하드립니다.");

        simpleMailMessage.setText(("""
                안녕하세요 'Grooveo'입니다.
                회원가입을 축하드립니다! %s님! 'Grooveo'에서는 다양한 서비스를 이용하실 수 있습니다.
                지금부터 'Grooveo' 서비스를 자유롭게 이용하실 수 있습니다.
                추가적인 문의사항이 있으시면 언제든지 저희 'Grooveo' 고객센터로 문의해주세요.""").formatted(member.getUsername()));

        javaMailSender.send(simpleMailMessage);
    }

}