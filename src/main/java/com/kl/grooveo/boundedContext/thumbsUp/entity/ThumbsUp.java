package com.kl.grooveo.boundedContext.thumbsUp.entity;

import com.kl.grooveo.boundedContext.community.entity.FreedomPost;
import com.kl.grooveo.boundedContext.member.entity.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class ThumbsUp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private FreedomPost freedomPost;

    @ManyToOne
    private Member member;

    private LocalDateTime createDate;
    private LocalDateTime deleteDate;
}
