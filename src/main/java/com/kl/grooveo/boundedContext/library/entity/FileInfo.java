package com.kl.grooveo.boundedContext.library.entity;

import com.kl.grooveo.boundedContext.comment.entity.SoundPostComment;
import com.kl.grooveo.boundedContext.member.entity.Member;
import com.kl.grooveo.boundedContext.thumbsUp.entity.SoundThumbsUp;
import com.kl.grooveo.boundedContext.thumbsUp.entity.SoundThumbsUp_summary;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "file_info")
@SuperBuilder
@NoArgsConstructor
public class FileInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @ManyToOne
    private Member artist;

    @Column(columnDefinition = "LONGTEXT")
    private String description;

    @Column(columnDefinition = "integer default 0", nullable = false)
    private int view; // 조회수

    @OneToMany(mappedBy = "fileInfo", cascade = CascadeType.REMOVE)
    private List<SoundPostComment> commentList = new ArrayList<>();

    @OneToMany(mappedBy = "fileInfo", cascade = CascadeType.REMOVE)
    private List<SoundThumbsUp> soundThumbsUpList = new ArrayList<>();

    @OneToOne(mappedBy = "fileInfo", cascade = CascadeType.REMOVE)
    private SoundThumbsUp_summary soundThumbsUpSummary;

    private String albumCoverUrl;
    private String soundUrl;

    private LocalDateTime createDate;
    private LocalDateTime modifyDate;

    public String getAfterPost() {
        long diff = ChronoUnit.SECONDS.between(getCreateDate(), LocalDateTime.now());
        if (diff < 60) return "방금 전";
        else if (diff < 3600) {
            return (diff / 60) + "분 전";
        } else if (diff < 86400) {
            return (diff / 60 / 60) + "시간 전";
        } else return getCreateDate().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
    }
}