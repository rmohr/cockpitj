package com.github.rmohr.cockpit.client;

import static org.assertj.core.api.Assertions.assertThat;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.websocket.MessageHandler;
import javax.websocket.WebSocketContainer;

import org.glassfish.tyrus.client.ClientManager;
import org.glassfish.tyrus.client.ClientProperties;
import org.glassfish.tyrus.client.SslEngineConfigurator;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClientTestBase {

    protected WebSocketContainer createInsecureSocketContainer() throws NoSuchAlgorithmException,
            KeyManagementException {
        ClientManager client = ClientManager.createClient();
        SslEngineConfigurator sslEngineConfigurator = new SslEngineConfigurator(SslUtils.createInsecureSslContext());
        sslEngineConfigurator.setHostnameVerifier(new SslUtils.AnyHostNameVerifier());
        client.getProperties().put(ClientProperties.SSL_ENGINE_CONFIGURATOR, sslEngineConfigurator);
        client.getProperties().put(ClientProperties.LOG_HTTP_UPGRADE, true);
        return client;
    }

    protected Client createInsecureClient(MessageHandler messageHandler)
            throws KeyManagementException, NoSuchAlgorithmException {
        Client client = new Client(createInsecureSocketContainer(),
                SslUtils.createInsecureHttpClient("root", "foobar"),
                "wss://localhost:9090/cockpit",
                messageHandler);
        return client;
    }

    protected String poll(BlockingQueue<String> queue) throws InterruptedException {
        String message = queue.poll(2, TimeUnit.SECONDS);
        assertThat(message).isNotNull();
        return message;
    }

    protected String pollChannel(BlockingQueue<String> queue, String channelId) throws InterruptedException {
        String message = poll(queue);
        return message.replaceFirst(channelId + "\n", "");
    }
}
