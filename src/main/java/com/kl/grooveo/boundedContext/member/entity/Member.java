package com.kl.grooveo.boundedContext.member.entity;

import com.kl.grooveo.base.baseEntity.BaseEntity;
import com.kl.grooveo.boundedContext.comment.entity.FreedomPostComment;
import com.kl.grooveo.boundedContext.community.entity.FreedomPost;
import com.kl.grooveo.boundedContext.follow.entity.Follow;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor
public class Member extends BaseEntity {

    @Column(unique = true)
    private String username;
    private String password;
    private String name;
    @Column(unique = true)
    private String nickName;
    @Column(unique = true)
    private String email;
    @Builder.Default
    private String role = "user";
    private String providerTypeCode;

    @OneToMany(mappedBy = "author", cascade = {CascadeType.ALL})
    private List<FreedomPost> freedomPosts = new ArrayList<>();

    @OneToMany(mappedBy = "author", cascade = {CascadeType.ALL})
    private List<FreedomPostComment> freedomPostComments = new ArrayList<>();

    @OneToMany(mappedBy = "follower", cascade = {CascadeType.ALL})
    @OrderBy("id desc")
    @Builder.Default
    private List<Follow> followerPeople = new ArrayList<>();

    @OneToMany(mappedBy = "following", cascade = {CascadeType.ALL})
    @OrderBy("id desc")
    @Builder.Default
    private List<Follow> followingPeople = new ArrayList<>();

    public List<? extends GrantedAuthority> getGrantedAuthorities() {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();

        grantedAuthorities.add(new SimpleGrantedAuthority("user"));

        if ("admin".equals(username)) {
            grantedAuthorities.add(new SimpleGrantedAuthority("admin"));
        }

        return grantedAuthorities;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateNickName(String nickName) {
        this.nickName = nickName;

    }

    public void updateEmail(String email) {
        this.email = email;
    }
}