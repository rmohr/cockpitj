package com.github.rmohr.cockpitj.systemd;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.websocket.DeploymentException;

import org.apache.http.auth.AuthenticationException;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.rmohr.cockpitj.core.ChannelClosedException;
import com.github.rmohr.cockpitj.core.Client;
import com.github.rmohr.cockpitj.core.channel.ControlCommandFactory;
import com.github.rmohr.cockpitj.core.channel.QueuingChannelReceiver;
import com.github.rmohr.cockpitj.core.handler.MultiplexingMessageHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SystemctlChannelTest extends ClientTestBase {

    private Client client;
    private SystemctlChannel channel;

    @Before
    public void setUp() throws IOException, DeploymentException, NoSuchAlgorithmException, AuthenticationException,
            URISyntaxException, KeyManagementException, InterruptedException {
        QueuingChannelReceiver channelReceiver = new QueuingChannelReceiver("systemd", 10000);
        MultiplexingMessageHandler messageHandler = new MultiplexingMessageHandler();
        messageHandler.addChannel(channelReceiver);
        client = createInsecureClient(messageHandler);
        client.connect();
        client.sendMessage(ControlCommandFactory.init());
        channel = new SystemctlChannel(client, channelReceiver, 10, TimeUnit.SECONDS);
    }

    @Test
    public void shouldGetListOfRunningServices()
            throws JsonProcessingException, InterruptedException, ChannelClosedException {
        assertThat(channel.open()).isTrue();
        List<Unit> activeUnits = channel.getLoadedUnits();
        assertThat(activeUnits).isNotEmpty();
    }


}
