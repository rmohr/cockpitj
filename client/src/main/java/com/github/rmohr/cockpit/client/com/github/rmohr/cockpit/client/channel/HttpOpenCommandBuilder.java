package com.github.rmohr.cockpit.client.com.github.rmohr.cockpit.client.channel;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


//{"method":"GET","path":"/v1.12/containers/2e6de48a23918423ab3577df95371e129b4344da1140830b2f52f55debad1f02/json",
//        "superuser":"try","payload":"http-stream1","unix":"/var/run/docker.sock","command":"open","channel":"2:3!9",
//        "host":"localhost","group":"cockpit1:localhost/docker"}
//2:3!9
//{"status":200,"reason":"OK","headers":{"Server":"Docker/1.9.1-fc23 (linux)","Date":"Wed, 13 Jan 2016 12:42:03 GMT",
//        "Content-Type":"application/json"}}
//2:3!9
//        {"Id":"2e6de48a23918423ab3577df95371e129b4344da1140830b2f52f55debad1f02","Created":"2016-01-08T12:03:50"
//        ".403801232Z","Path":"node","Args":[],"State":{"Status":"exited","Running":false,"Paused":false,
//        "Restarting":false,"OOMKilled":false,"Dead":false,"Pid":0,"ExitCode":137,"Error":"",
//        "StartedAt":"2016-01-08T20:07:46.507236439Z","FinishedAt":"2016-01-09T19:38:10.149231456Z"},
//        "Image":"ac9b478bfbbd7ffc433af7db6729f9039857367aa0aef2506edc6717f5acd967",
//        "ResolvConfPath":"/var/lib/docker/containers"...}}

public class HttpOpenCommandBuilder extends OpenCommandBuilder<HttpOpenCommandBuilder> {

    private List<String> headers = new ArrayList<>();

    protected HttpOpenCommandBuilder(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    public static HttpOpenCommandBuilder builder(ObjectMapper objectMapper) {
        HttpOpenCommandBuilder dbusCommandBuilder = new HttpOpenCommandBuilder(objectMapper);
        dbusCommandBuilder.setSelf(dbusCommandBuilder);
        return dbusCommandBuilder;
    }

    public static HttpOpenCommandBuilder builder() {
        return builder(new ObjectMapper());
    }

    public HttpOpenCommandBuilder socket(String socket) {
        command.put("unix", socket);
        return this;
    }

    public HttpOpenCommandBuilder port(String port) {
        command.put("port", port);
        return this;
    }

    public HttpOpenCommandBuilder method(String method) {
        command.put("method", method);
        return this;
    }

    public HttpOpenCommandBuilder path(String path) {
        command.put("path", path);
        return this;
    }

    public HttpOpenCommandBuilder address(String address) {
        command.put("address", address);
        return this;
    }

    @Override
    public String build() throws JsonProcessingException {
        payload("http-stream1");
        return super.build();
    }
}
