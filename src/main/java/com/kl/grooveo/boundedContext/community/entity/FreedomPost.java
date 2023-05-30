package com.kl.grooveo.boundedContext.community.entity;

import com.kl.grooveo.boundedContext.comment.entity.FreedomPostComment;
import com.kl.grooveo.boundedContext.member.entity.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;
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

    public String categoryDisplayName() {
        return switch (category) {
            case "" -> "전체";
            case "c2" -> "음악";
            case "c3" -> "리뷰";
            case "c4" -> "가사 해석";
            default -> "인증/후기";
        };
    }
}
