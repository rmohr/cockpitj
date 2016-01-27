package com.github.rmohr.cockpitj.core.channel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DbusOpenCommandBuilder extends OpenCommandBuilder<DbusOpenCommandBuilder> {

    protected DbusOpenCommandBuilder(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    public static DbusOpenCommandBuilder builder(ObjectMapper objectMapper) {
        DbusOpenCommandBuilder dbusCommandBuilder = new DbusOpenCommandBuilder(objectMapper);
        dbusCommandBuilder.setSelf(dbusCommandBuilder);
        return dbusCommandBuilder;
    }

    public static DbusOpenCommandBuilder builder() {
        return builder(new ObjectMapper());
    }

    public DbusOpenCommandBuilder system() {
        command.put("bus", "system");
        return this;
    }

    public DbusOpenCommandBuilder session() {
        command.put("bus", "system");
        return this;
    }

    public DbusOpenCommandBuilder internal() {
        command.put("bus", "internal");
        return this;
    }

    public DbusOpenCommandBuilder name(String name) {
        command.put("name", name);
        return this;
    }

    public DbusOpenCommandBuilder address(String address) {
        command.put("address", address);
        return this;
    }

    @Override public String build() throws JsonProcessingException {
        payload("dbus-json3");
        return super.build();
    }
}
