package com.github.rmohr.cockpitj.core.channel;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ControlCommandFactory {

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static String done(String channelId) {
        return String.format("\n{ \"command\" : \"done\", \"channel\": \"%s\"}", channelId);
    }

    public static String close(String channelId) {
        return String.format("\n{ \"command\" : \"close\", \"channel\": \"%s\"}", channelId);
    }

    public static String init() {
        return "\n{ \"command\": \"init\", \"version\": 1 }";
    }

    public static String ping() {
        return "\n{ \"command\": \"ping\" }";
    }

    public static String disconnect() {
        return "\n{ \"command\": \"logout\", \"disconnect\": true }";
    }

    public static String logout() {
        return "\n{ \"command\": \"logout\", \"disconnect\": false }";
    }

    public static String kill(String group) {
        return String.format("\n{ \"command\": \"kill\", \"group\": \'%s\' }", group);
    }

    public static String killAll() {
        return "\n{ \"command\": \"kill\" }";
    }
}
