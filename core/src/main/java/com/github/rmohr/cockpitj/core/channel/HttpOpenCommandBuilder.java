package com.github.rmohr.cockpitj.core.channel;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class HttpOpenCommandBuilder extends OpenCommandBuilder<HttpOpenCommandBuilder> {

    private List<String> headers = new ArrayList<>();

    protected HttpOpenCommandBuilder(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    public static HttpOpenCommandBuilder builder(ObjectMapper objectMapper) {
        HttpOpenCommandBuilder dbusCommandBuilder = new HttpOpenCommandBuilder(objectMapper);
        dbusCommandBuilder.setSelf(dbusCommandBuilder);
        return dbusCommandBuilder;
    }

    public static HttpOpenCommandBuilder builder() {
        return builder(new ObjectMapper());
    }

    public HttpOpenCommandBuilder socket(String socket) {
        command.put("unix", socket);
        return this;
    }

    public HttpOpenCommandBuilder port(String port) {
        command.put("port", port);
        return this;
    }

    public HttpOpenCommandBuilder method(String method) {
        command.put("method", method);
        return this;
    }

    public HttpOpenCommandBuilder path(String path) {
        command.put("path", path);
        return this;
    }

    public HttpOpenCommandBuilder address(String address) {
        command.put("address", address);
        return this;
    }

    @Override
    public String build() throws JsonProcessingException {
        payload("http-stream1");
        return super.build();
    }
}
