package com.kl.grooveo.boundedContext.reply.entity;

import com.kl.grooveo.boundedContext.comment.entity.SoundPostComment;
import com.kl.grooveo.boundedContext.member.entity.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class SoundPostReply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private SoundPostComment soundPostComment;

    @ManyToOne
    private Member author;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime createDate;
    private LocalDateTime deleteDate;
}
