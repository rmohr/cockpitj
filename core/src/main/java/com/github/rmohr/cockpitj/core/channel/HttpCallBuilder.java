package com.github.rmohr.cockpitj.core.channel;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class HttpCallBuilder {

    private final ObjectMapper objectMapper;
    private String channelId;
    private Object body;
    private Map<String, Object> headerBody = new HashMap<>();
    private Map<String, String> headers = new HashMap<>();

    protected HttpCallBuilder(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public static HttpCallBuilder builder() {
        return builder(new ObjectMapper());
    }

    public static HttpCallBuilder builder(ObjectMapper objectMapper) {
        return new HttpCallBuilder(objectMapper);
    }

    public HttpCallBuilder channel(String channelId) {
        this.channelId = channelId;
        return this;
    }

    public HttpCallBuilder body(Object body) {
        this.body = body;
        return this;
    }

    public HttpCallBuilder status(int httpStatus) {
        headerBody.put("status", httpStatus);
        return this;
    }

    public HttpCallBuilder reason(String reason) {
        headerBody.put("reason", reason);
        return this;
    }

    public HttpCallBuilder addHeader(String key, String value) {
        headers.put(key, value);
        return this;
    }

    public String buildHeaders() throws JsonProcessingException {
        headerBody.put("headers", headers);
        return channelId + "\n" + objectMapper.writeValueAsString(headerBody);
    }

    public String buildBody() throws JsonProcessingException {
        return channelId + "\n" + objectMapper.writeValueAsString(body);
    }
}
