package com.kl.grooveo.boundedContext.thumbsUp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ThumbsUp_summary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private Long postId;

    @Column(nullable = false)
    private int likeCount = 0;
}