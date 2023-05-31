package com.kl.grooveo.boundedContext.community.entity;

import com.kl.grooveo.boundedContext.comment.entity.FreedomPostComment;
import com.kl.grooveo.boundedContext.member.entity.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class FreedomPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 0 : 전체, 1 : 음악, 2 : 리뷰, 3 : 가사 해석, 4 : 인증/후기
    private String category;

    @Column
    private String title;

    @ManyToOne
    private Member author;

    @Column(columnDefinition = "TEXT")
    private String content;

    @OneToMany(mappedBy = "freedomPost", cascade = CascadeType.REMOVE)
    private List<FreedomPostComment> commentList = new ArrayList<>();

    // 1 : 국외 게시판 2: 국내 게시판
    private Integer boardType;

    private LocalDateTime createDate;
    private LocalDateTime modifyDate;
    private LocalDateTime deleteDate;

    @Column(columnDefinition = "integer default 0", nullable = false)
    private int view; // 조회수

    public String categoryDisplayName() {
        return switch (category) {
            case "" -> "전체";
            case "c2" -> "음악";
            case "c3" -> "리뷰";
            case "c4" -> "가사 해석";
            default -> "인증/후기";
        };
    }

    public String getAfterPost() {
        long diff = ChronoUnit.SECONDS.between(getCreateDate(), LocalDateTime.now());
        if (diff < 60) return "방금 전";
        else if (diff < 3600) {
            return (diff / 60) + "분 전";
        }
        else if (diff < 86400) {
            return (diff / 60 / 60) + "시간 전";
        }
        else return getCreateDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}
