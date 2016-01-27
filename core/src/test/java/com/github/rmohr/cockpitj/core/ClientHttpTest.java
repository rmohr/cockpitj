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
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.rmohr.cockpitj.core.channel.ControlCommandFactory;
import com.github.rmohr.cockpitj.core.channel.HttpOpenCommandBuilder;
import com.github.rmohr.cockpitj.core.handler.QueuingMessageHandler;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;

public class ClientHttpTest extends ClientTestBase {

    private BlockingQueue<String> queue;
    private Client client;
    private Configuration config;

    @Before
    public void setUp() throws IOException, DeploymentException, NoSuchAlgorithmException, AuthenticationException,
            URISyntaxException, KeyManagementException, InterruptedException {
        queue = new LinkedBlockingQueue<>(10000);
        config = Configuration.defaultConfiguration().mappingProvider(new JacksonMappingProvider())
                .jsonProvider(new JacksonJsonProvider()).addOptions(Option.DEFAULT_PATH_LEAF_TO_NULL);

        client = createInsecureClient(new QueuingMessageHandler(queue));
        client.connect();
        client.sendMessage(ControlCommandFactory.init());
        assertThat(JsonPath.read(poll(queue), "$.command")).isEqualTo("init");
    }

    @Test
    @Ignore
    public void shouldGetDockerInfo() throws JsonProcessingException, InterruptedException {
        client.sendMessage(HttpOpenCommandBuilder.builder()
                .group("cockpit1:localhost/docker")
                .channel("docker")
                .host("localhost")
                .socket("/var/run/docker.sock")
                .path("/v1.12/info")
                .method("GET")
                .build());
        assertThat(JsonPath.read(poll(queue), "$.command")).isEqualTo("ready");
        client.sendMessage(ControlCommandFactory.done("docker"));

        assertThat(JsonPath.using(config).parse(pollChannel(queue, "docker")).read("$.status")).isEqualTo(200);
        assertThat(JsonPath.using(config).parse(pollChannel(queue, "docker")).read("$.Containers")).isNotNull();
        assertThat(JsonPath.read(poll(queue), "$.command")).isEqualTo("done");
        assertThat(JsonPath.read(poll(queue), "$.command")).isEqualTo("close");
    }

    @After
    public void tearDown() {
        client.sendMessage(ControlCommandFactory.done("test"));
        client.sendMessage(ControlCommandFactory.disconnect());
    }
}
