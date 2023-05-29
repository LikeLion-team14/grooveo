package com.kl.grooveo.base.email.service;

import com.kl.grooveo.boundedContext.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;

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

    public void sendUsername(Member member) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

        simpleMailMessage.setTo(member.getEmail());

        simpleMailMessage.setSubject("아이디 찾기");

        simpleMailMessage.setText(("""
                안녕하세요 'Grooveo'입니다."
                회원님이 요청하신 아이디 찾기 안내 메시지를 보내드립니다.
                회원님의 아이디는 다음과 같습니다: [%s]. 감사합니다.""").formatted(member.getUsername()));

        javaMailSender.send(simpleMailMessage);
    }

    // 임시 비밀번호 만들기
    private String createCode() {
        Random random = new Random();
        StringBuffer key = new StringBuffer();

        for (int i = 0; i < 8; i++) {
            int index = random.nextInt(4);

            switch (index) {
                case 0:
                    key.append((char) (random.nextInt(26) + 97));
                    break;
                case 1:
                    key.append((char) (random.nextInt(26) + 65));
                    break;
                default:
                    key.append(random.nextInt(9));
            }
        }
        return key.toString();
    }

    public String sendTemporaryPassword(Member member) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

        simpleMailMessage.setTo(member.getEmail());

        simpleMailMessage.setSubject("임시비밀번호 발급");

        String temporaryPassword = createCode();

        simpleMailMessage.setText(("""
                안녕하세요 'Grooveo' 입니다.
                비밀번호 분실을 처리하기 위해 임시 비밀번호를 발급해드렸습니다.
                임시 비밀번호는 다음과 같습니다: [%s].
                보안을 위해 로그인 후 반드시 비밀번호를 변경해주세요.""").formatted(temporaryPassword));

        javaMailSender.send(simpleMailMessage);

        return temporaryPassword;
    }
}