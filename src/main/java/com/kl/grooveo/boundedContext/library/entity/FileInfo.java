package com.kl.grooveo.boundedContext.library.entity;

import com.kl.grooveo.boundedContext.member.entity.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

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

    private String description;
    private String albumCoverUrl;
    private String soundUrl;

    private LocalDateTime createDate;
}