package com.github.rmohr.cockpitj.systemd;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.websocket.DeploymentException;

import org.apache.http.auth.AuthenticationException;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.rmohr.cockpitj.core.Client;
import com.github.rmohr.cockpitj.core.channel.ControlCommandFactory;
import com.github.rmohr.cockpitj.core.channel.DbusCallBuilder;
import com.github.rmohr.cockpitj.core.channel.DbusOpenCommandBuilder;
import com.github.rmohr.cockpitj.core.handler.QueuingMessageHandler;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.TypeRef;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UnitTest extends ClientTestBase {

    private BlockingQueue<String> queue;
    private Client client;

    @Before
    public void setUp() throws IOException, DeploymentException, NoSuchAlgorithmException, AuthenticationException,
            URISyntaxException, KeyManagementException, InterruptedException {
        queue = new LinkedBlockingQueue<>(10000);
        client = createInsecureClient(new QueuingMessageHandler(queue));
        client.connect();
        client.sendMessage(ControlCommandFactory.init());
        client.sendMessage(DbusOpenCommandBuilder.builder()
                .name(null)
                .group("cockpit1:localhost/system")
                .channel("test")
                .host("localhost")
                .name("org.freedesktop.systemd1")
                .build());
        assertThat(JsonPath.read(poll(queue), "$.command")).isEqualTo("init");
        assertThat(JsonPath.read(poll(queue), "$.command")).isEqualTo("ready");
        assertThat(JsonPath.read(pollChannel(queue, "test"), "$.owner")).isEqualTo(":1.0");
    }

    @Test
    public void shouldGetListOfRunningServices() throws JsonProcessingException, InterruptedException {
        client.sendMessage(DbusCallBuilder.builder().id(UUID.randomUUID().toString())
                .method("ListUnits")
                .dbusInterface("org.freedesktop.systemd1.Manager")
                .path("/org/freedesktop/systemd1")
                .channel("test")
                .build());
        String message = pollChannel(queue, "test");
        Configuration config = Configuration.defaultConfiguration().mappingProvider(new JacksonMappingProvider())
                .jsonProvider(new JacksonJsonProvider()).addOptions(Option.DEFAULT_PATH_LEAF_TO_NULL);
        TypeRef<List<Unit>> type = new TypeRef<List<Unit>>(){};
        assertThat(JsonPath.using(config).parse(message).read("$.error")).isNull();
        List<Unit> activeUnits =
                JsonPath.using(config).parse(message).read("$.reply[*][*][?(@[3] == 'active')]", type);
        log.debug(message);
        assertThat(activeUnits).isNotEmpty();
    }


}
