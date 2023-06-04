package com.kl.grooveo.boundedContext.comment.entity;

import com.kl.grooveo.boundedContext.community.entity.FreedomPost;
import com.kl.grooveo.boundedContext.member.entity.Member;
import com.kl.grooveo.boundedContext.reply.entity.FreedomPostReply;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Getter
@Setter
@Entity
public class FreedomPostComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private FreedomPost freedomPost;

    @ManyToOne
    private Member author;

    @Column(columnDefinition = "TEXT")
    private String content;

    @OneToMany(mappedBy = "freedomPostComment", cascade = CascadeType.REMOVE)
    private List<FreedomPostReply> replyList;

    private LocalDateTime createDate;
    private LocalDateTime deleteDate;

    public String getAfterPost() {
        long diff = ChronoUnit.SECONDS.between(getCreateDate(), LocalDateTime.now());
        if (diff < 60) return "방금 전";
        else if (diff < 3600) {
            return (diff / 60) + "분 전";
        } else if (diff < 86400) {
            return (diff / 60 / 60) + "시간 전";
        } else return getCreateDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}
