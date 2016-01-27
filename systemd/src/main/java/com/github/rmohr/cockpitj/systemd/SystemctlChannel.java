package com.github.rmohr.cockpitj.systemd;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.rmohr.cockpitj.core.ChannelClosedException;
import com.github.rmohr.cockpitj.core.Client;
import com.github.rmohr.cockpitj.core.channel.DbusCallBuilder;
import com.github.rmohr.cockpitj.core.channel.DbusOpenCommandBuilder;
import com.github.rmohr.cockpitj.core.channel.QueuingChannelReceiver;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.TypeRef;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class SystemctlChannel {

    private final BlockingQueue<String> queue;
    private final Client client;
    private final String channel;
    private final QueuingChannelReceiver receiver;
    private TimeUnit timeUnit;
    private long timeout;
    private Configuration config= Configuration.defaultConfiguration().mappingProvider(new JacksonMappingProvider())
                .jsonProvider(new JacksonJsonProvider()).addOptions(Option.DEFAULT_PATH_LEAF_TO_NULL);

    public boolean open(String host) throws JsonProcessingException {
        client.sendMessage(DbusOpenCommandBuilder.builder()
                .name(null)
                .group("cockpit1:localhost/system")
                .channel(channel)
                .host(host)
                .name("org.freedesktop.systemd1")
                .build());
        return receiver.waitForChannel(timeout, timeUnit);
    }

    public SystemctlChannel(Client client, QueuingChannelReceiver receiver, int messageCapacity, long timeout, TimeUnit
            timeUnit) {
        queue = new LinkedBlockingDeque<>(messageCapacity);
        this.client = client;
        this.channel = UUID.randomUUID().toString();
        this.timeout = timeout;
        this.timeUnit = timeUnit;
        this.receiver = receiver;
    }

    public List<Unit> getLoadedUnits() throws JsonProcessingException, ChannelClosedException {
        client.sendMessage(DbusCallBuilder.builder().id(UUID.randomUUID().toString())
                .method("ListUnits")
                .dbusInterface("org.freedesktop.systemd1.Manager")
                .path("/org/freedesktop/systemd1")
                .channel("test")
                .build());
        String message = receiver.pollMessage(timeout, timeUnit);
        if (message == null) {
            throw new ChannelClosedException();
        }

        TypeRef<List<Unit>> type = new TypeRef<List<Unit>>(){};
        return JsonPath.using(config).parse(message).read("$.reply[*][*][*]", type);
    }

    public boolean stopUnit(Unit unit) {
        throw new NotImplementedException();
    }

    public boolean startUnit(Unit unit) {
        throw new NotImplementedException();
    }

    public boolean isOpen() {
        throw new NotImplementedException();
    }

}
