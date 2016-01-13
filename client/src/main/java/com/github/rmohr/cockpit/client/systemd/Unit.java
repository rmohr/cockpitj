package com.github.rmohr.cockpit.client.systemd;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(of = {"name"})
public class Unit {

    @JsonCreator
    public Unit(List<String> properties) {
        name = properties.get(0);
        description = properties.get(1);
        load = properties.get(2);
        active = properties.get(3);
        activationState = properties.get(4);
        objectPath = properties.get(6);
    }

    private String name;

    private String description;

    private String load;

    private String active;

    private String activationState;

    private String objectPath;

    public boolean isLoaded() {
        return "loaded".equals(load);
    }

    public boolean isActive() {
        return "active".equals(active);
    }

    public boolean isRunning() {
        return "running".equals(activationState);
    }

    public boolean isStopped() {
        return "exited".equals(activationState);
    }

    public boolean isFailing() {
        return "failing".equals(active) || "failing".equals(activationState);
    }

}
