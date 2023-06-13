package com.kl.grooveo.base.event;

import com.kl.grooveo.boundedContext.follow.entity.Follow;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class EventAfterUpload extends ApplicationEvent {
    private final Follow follow;

    public EventAfterUpload(Object source, Follow follow) {
        super(source);
        this.follow = follow;
    }
}