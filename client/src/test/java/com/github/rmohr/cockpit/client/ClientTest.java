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
import org.glassfish.tyrus.client.SslEngineConfigurator;
import org.junit.Test;

public class ClientTest {

    @Test
    public void testConnection() throws URISyntaxException, IOException, DeploymentException, KeyManagementException,
            NoSuchAlgorithmException, InterruptedException, AuthenticationException {
        Client client = new Client(createInsecureSocketContainer(),
                SslUtils.createInsecureHttpClient("root", "foobar"),
                "wss://localhost:9090/cockpit",
                new NoopMessageHandler());
        client.connect();
        client.sendMessage("{\"command\":\"init\",\"version\":1 }");
    }

    private WebSocketContainer createInsecureSocketContainer() throws NoSuchAlgorithmException, KeyManagementException {
        ClientManager client = ClientManager.createClient();
        SslEngineConfigurator sslEngineConfigurator = new SslEngineConfigurator(SslUtils.createInsecureSslContext());
        sslEngineConfigurator.setHostnameVerifier(new SslUtils.AnyHostNameVerifier());
        client.getProperties().put(ClientProperties.SSL_ENGINE_CONFIGURATOR, sslEngineConfigurator);
        client.getProperties().put(ClientProperties.LOG_HTTP_UPGRADE, true);
        return client;
    }
}
