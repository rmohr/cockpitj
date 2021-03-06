package com.github.rmohr.cockpitj.core.handler;

import javax.websocket.MessageHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NoopMessageHandler implements MessageHandler.Whole<String> {
    public void onMessage(String s) {
        log.debug(s);
    }
}
