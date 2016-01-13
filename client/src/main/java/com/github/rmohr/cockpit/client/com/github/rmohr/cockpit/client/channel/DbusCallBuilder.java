package com.github.rmohr.cockpit.client.com.github.rmohr.cockpit.client.channel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/*gdbus call --system --dest org.freedesktop.systemd1 --object-path /org/freedesktop/systemd1 --method org
.freedesktop.systemd1.Manager.ListUnits*/
public class DbusCallBuilder {

    private final ObjectMapper objectMapper;
    private String path;
    private List<String> arguments;
    private String dbusInterface;
    private String method;
    private String id;
    private String channelId;

    protected DbusCallBuilder(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public static DbusCallBuilder builder() {
       return builder(new ObjectMapper());
    }
    public static DbusCallBuilder builder(ObjectMapper objectMapper) {
        return new DbusCallBuilder(objectMapper);
    }

    public DbusCallBuilder path(String path) {
        this.path = path;
        return this;
    }

    public DbusCallBuilder arguments(List<String> arguments) {
        this.arguments = arguments;
        return this;
    }

    public DbusCallBuilder dbusInterface(String dbusInterface) {
        this.dbusInterface = dbusInterface;
        return this;
    }

    public DbusCallBuilder method(String method) {
        this.method = method;
        return this;
    }

    public DbusCallBuilder id(String id) {
        this.id = id;
        return this;
    }

    public DbusCallBuilder channel(String channelId){
       this.channelId = channelId;
        return this;
    }

    public String build() throws JsonProcessingException {
        Map<String, Object> command = new HashMap<>();
        if (id != null) {
            command.put("id", id);
        }
        if (arguments == null) {
            arguments = new ArrayList<>();
        }
        command.put("call", Arrays.asList(path, dbusInterface, method, arguments));
        return channelId + "\n" + objectMapper.writeValueAsString(command);
    }
}
