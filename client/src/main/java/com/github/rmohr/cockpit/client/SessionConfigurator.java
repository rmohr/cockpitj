package com.github.rmohr.cockpit.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.websocket.ClientEndpointConfig;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SessionConfigurator extends ClientEndpointConfig.Configurator {

    private final String sessionCookie;

    public SessionConfigurator(final String sessionCookie) {
        this.sessionCookie = sessionCookie;
    }

    @Override
    public void beforeRequest(Map<String, List<String>> headers) {
        super.beforeRequest(headers);
        headers.put("Cookie", Arrays.asList(sessionCookie));
        headers.put("Origin", new ArrayList<String>(Arrays.asList("http://" + headers.get("Origin").get(0))));
    }

}
