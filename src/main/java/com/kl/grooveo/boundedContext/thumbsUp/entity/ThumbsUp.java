package com.kl.grooveo.boundedContext.thumbsUp.entity;

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

    private Long postId;

    @ManyToOne
    private Member member;

    private LocalDateTime createDate;
    private LocalDateTime deleteDate;

    public ThumbsUp() {
    }

    public ThumbsUp(Long postId, Member member) {
        this.postId = postId;
        this.member = member;
    }
}
