package com.kl.grooveo.boundedContext.community.entity;

import com.kl.grooveo.boundedContext.comment.entity.FreedomPostComment;
import com.kl.grooveo.boundedContext.member.entity.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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

    @Column
    private String category;

    @Column
    private String title;

    @ManyToOne
    private Member author;

    @Column(columnDefinition = "TEXT")
    private String content;

    @OneToMany(mappedBy = "freedomPost", cascade = CascadeType.REMOVE)
    private List<FreedomPostComment> commentList = new ArrayList<>();

    private LocalDateTime createDate;
    private LocalDateTime modifyDate;
    private LocalDateTime deleteDate;
}
