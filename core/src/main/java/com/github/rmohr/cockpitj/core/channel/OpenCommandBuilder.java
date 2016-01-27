package com.github.rmohr.cockpitj.core.channel;

/*
{"bus":"internal","payload":"dbus-json3","name":null,"command":"open","channel":"3:4","host":"localhost"}
*/

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class OpenCommandBuilder<T> {


    private T self;
    protected Map<String, Object> command = new HashMap<>();
    protected ObjectMapper objectMapper;

    protected OpenCommandBuilder(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    protected void setSelf(T self){
        this.self = self;
    }

    public T binary() {
        command.put("binary", "base64");
        return self;
    }

    public T raw() {
        command.put("binary", "raw");
        return self;
    }

    public T text() {
        command.remove("binary");
        return self;
    }

    public T channel(final String channelId) {
        command.put("channel", channelId);
        return self;
    }

    public T payload(String payload) {
        command.put("payload", payload);
        return self;
    }

    public T host(String host) {
        command.put("host", host);
        return self;
    }

    public T group(String group) {
        command.put("group", group);
        return self;
    }

    public T capabilities(List<String> capabilities) {
       command.put("Capabilities", capabilities);
        return self;
    }

    public String build() throws JsonProcessingException {
        command.put("command", "open");
        return "\n" +  objectMapper.writeValueAsString(command);
    }
}
