package com.github.rmohr.cockpitj.core.channel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@AllArgsConstructor
@Builder
public class Message {

    private String body;
    private boolean controlCommand;
    private String channel;
    private boolean valid;
    private String command;
}
