package com.github.rmohr.cockpitj.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.websocket.ClientEndpointConfig;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SessionConfigurator extends ClientEndpointConfig.Configurator {

    private final String sessionCookie;
    private String protocolPrefix;

    public SessionConfigurator(final String sessionCookie, boolean isSsl) {
        this.sessionCookie = sessionCookie;
        if (isSsl) {
            protocolPrefix = "https://";
        } else {
            protocolPrefix = "http://";
        }
    }

    @Override
    public void beforeRequest(Map<String, List<String>> headers) {
        super.beforeRequest(headers);
        headers.put("Cookie", Arrays.asList(sessionCookie));
        headers.put("Origin", new ArrayList<>(Arrays.asList(protocolPrefix + headers.get("Origin").get(0))));
    }

}
