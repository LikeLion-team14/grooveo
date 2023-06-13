package com.kl.grooveo.boundedContext.library.entity;

import com.kl.grooveo.boundedContext.comment.entity.SoundPostComment;
import com.kl.grooveo.boundedContext.member.entity.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "file_info")
@SuperBuilder
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class FileInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Member author;

    private String title;
    private String description;
    private String albumCoverUrl;
    private String soundUrl;

    @CreatedDate
    private LocalDateTime createDate;
    @LastModifiedDate
    private LocalDateTime modifyDate;

    @OneToMany(mappedBy = "fileInfo", cascade = CascadeType.REMOVE)
    private List<SoundPostComment> commentList = new ArrayList<>();

}