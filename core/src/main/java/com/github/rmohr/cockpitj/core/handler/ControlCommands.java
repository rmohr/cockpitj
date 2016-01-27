package com.github.rmohr.cockpitj.core.handler;

import java.util.Arrays;
import java.util.List;

public interface ControlCommands {

    String INIT = "init";
    String CLOSE = "close";
    String DONE = "done";
    String OPEN = "open";
    String KILL = "kill";
    String LOGOUT = "logout";
    String PING = "ping";
    String READY = "ready";
    List<String> channelCommands = Arrays.asList(READY, DONE, CLOSE);
}
