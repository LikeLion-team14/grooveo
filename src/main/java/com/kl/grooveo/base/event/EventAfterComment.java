package com.kl.grooveo.base.event;

import com.kl.grooveo.boundedContext.member.entity.Member;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class EventAfterComment extends ApplicationEvent {
    private final Member postAuthor;
    private final Member commentAuthor;

    public EventAfterComment(Object source, Member commentAuthor, Member postAuthor) {
        super(source);
        this.commentAuthor = commentAuthor;
        this.postAuthor = postAuthor;
    }
}
