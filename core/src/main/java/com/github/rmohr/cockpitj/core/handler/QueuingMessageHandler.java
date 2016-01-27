package com.github.rmohr.cockpitj.core.handler;

import java.util.Queue;

import javax.websocket.MessageHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QueuingMessageHandler implements MessageHandler.Whole<String> {

    private final Queue<String> queue;

    public QueuingMessageHandler(Queue<String> queue) {
        this.queue = queue;
    }

    @Override
    public void onMessage(String s) {
        log.debug("Receiving {}", s);
        queue.add(s);
    }
}
