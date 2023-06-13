package com.kl.grooveo.boundedContext.notification.entity;

import com.kl.grooveo.boundedContext.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static jakarta.persistence.GenerationType.IDENTITY;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@ToString
@Entity
@Getter
public class Notification {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne
    @ToString.Exclude
    private Member toMember; // 메세지 받는 사람
    @ManyToOne
    @ToString.Exclude
    private Member fromMember; // 메세지 보낸 사람

    private String typeCode;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime readDate;

    @CreatedDate
    private LocalDateTime createDate;
    private LocalDateTime deleteDate;


    public String getAfterAddNotification() {
        long diff = ChronoUnit.SECONDS.between(getCreateDate(), LocalDateTime.now());
        if (diff < 60) return diff + "초";
        else if (diff < 3600) {
            return (diff / 60) + "분";
        } else if (diff < 86400) {
            return (diff / 60 / 60) + "시간";
        } else return (diff / 60 / 60 / 24) + "일";
    }

    public void setAfterReadNotification(LocalDateTime localDateTime) {
        this.readDate = localDateTime;
    }
}
