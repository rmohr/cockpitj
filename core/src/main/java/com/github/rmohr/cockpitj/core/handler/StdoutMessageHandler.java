package com.github.rmohr.cockpitj.core.handler;

import javax.websocket.MessageHandler;

public class StdoutMessageHandler implements MessageHandler.Whole<String> {
    public void onMessage(String s) {
        if (!s.contains("{\"command\":\"ping\"}")) {
            System.out.println(s);
        }
    }
}
