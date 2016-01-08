package com.github.rmohr.cockpit.debugger;

import javax.websocket.MessageHandler;

public class ConsoleMessageHandler implements MessageHandler.Whole<String> {
    public void onMessage(String s) {
        if (!s.contains("{\"command\":\"ping\"}")) {
            System.out.println(s);
        }
    }
}
