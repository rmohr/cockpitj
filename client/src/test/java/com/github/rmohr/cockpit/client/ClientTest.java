package com.github.rmohr.cockpit.client;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.websocket.DeploymentException;
import javax.websocket.WebSocketContainer;

import org.apache.http.auth.AuthenticationException;
import org.glassfish.tyrus.client.ClientManager;
import org.glassfish.tyrus.client.ClientProperties;
import org.junit.Test;

public class ClientTest {

    @Test
    public void testConnection() throws URISyntaxException, IOException, DeploymentException, KeyManagementException,
            NoSuchAlgorithmException, InterruptedException, AuthenticationException {
        new Client(getSocketContainer(), "ws://localhost:9090/cockpit", "root", "foobar", new NoopMessageHandler());
    }

    private WebSocketContainer getSocketContainer() throws NoSuchAlgorithmException, KeyManagementException {
        System.getProperties().put("javax.net.debug", "all");
        ClientManager client = ClientManager.createClient();
        client.getProperties().put(ClientProperties.LOG_HTTP_UPGRADE, true);
        return client;
    }
}
