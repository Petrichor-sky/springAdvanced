package com.springLearning;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class Listener1 {
    private static final Logger log = LoggerFactory.getLogger(Listener1.class);

    @EventListener
    public void aaa(UserRegisterEvent event){
        log.debug("{}",event);
    }
}
