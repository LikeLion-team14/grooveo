package com.kl.grooveo.base.initData;

import com.kl.grooveo.boundedContext.member.entity.Member;
import com.kl.grooveo.boundedContext.member.service.MemberService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@Profile({"dev"})
public class NotProd {

    @Bean
    CommandLineRunner initData(
            MemberService memberService
    ) {
        return new CommandLineRunner() {
            @Override
            @Transactional
            public void run(String... args) throws Exception {
                Member memberUser1 = memberService.join("user1", "1234", "유저1", "유유", "uu@naver.com").getData();
            }
        };
    }
}