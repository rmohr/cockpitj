package com.github.rmohr.cockpitj.core;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.websocket.DeploymentException;

import org.apache.http.auth.AuthenticationException;
import org.junit.Test;

import com.github.rmohr.cockpitj.core.channel.ControlCommandFactory;
import com.github.rmohr.cockpitj.core.channel.DbusOpenCommandBuilder;
import com.github.rmohr.cockpitj.core.handler.NoopMessageHandler;
import com.github.rmohr.cockpitj.core.handler.QueuingMessageHandler;
import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClientTest extends ClientTestBase {

    @Test
    public void shouldConnectSuccessfullyWithTls() throws URISyntaxException, IOException,
            DeploymentException,
            KeyManagementException,
            NoSuchAlgorithmException, InterruptedException, AuthenticationException {
        Client client = createInsecureClient(new NoopMessageHandler());
        client.connect();
    }

    @Test
    public void shouldOpenDbusChannel()
            throws NoSuchAlgorithmException, KeyManagementException, IOException, DeploymentException,
            AuthenticationException, URISyntaxException, InterruptedException {
        BlockingQueue<String> queue = new LinkedBlockingQueue<>(10000);
        Client client = createInsecureClient(new QueuingMessageHandler(queue));
        client.connect();

        client.sendMessage(ControlCommandFactory.init());
        client.sendMessage(DbusOpenCommandBuilder.builder().internal().name(null).channel("test").host("localhost")
                .build());
        assertThat(JsonPath.read(poll(queue), "$.command")).isEqualTo("init");
        assertThat(JsonPath.read(poll(queue), "$.command")).isEqualTo("ready");
        client.sendMessage(ControlCommandFactory.close("test"));
        client.sendMessage(ControlCommandFactory.disconnect());
    }
}
