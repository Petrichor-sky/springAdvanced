package com.springLearning;

import org.springframework.context.ApplicationEvent;


public class UserRegisterEvent extends ApplicationEvent {
    public UserRegisterEvent(Object source) {
        super(source);
    }
}
