package com.kl.grooveo.boundedContext.library.entity;

import com.kl.grooveo.boundedContext.comment.entity.SoundPostComment;
import com.kl.grooveo.boundedContext.member.entity.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
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

    @OneToMany(mappedBy = "fileInfo", cascade = CascadeType.REMOVE)
    private List<SoundPostComment> commentList = new ArrayList<>();

    private String albumCoverUrl;
    private String soundUrl;

    private LocalDateTime createDate;
    private LocalDateTime modifyDate;
}