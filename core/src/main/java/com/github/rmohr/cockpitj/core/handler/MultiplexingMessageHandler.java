package com.github.rmohr.cockpitj.core.handler;

import static com.github.rmohr.cockpitj.core.handler.ControlCommands.CLOSE;
import static com.github.rmohr.cockpitj.core.handler.ControlCommands.DONE;
import static com.github.rmohr.cockpitj.core.handler.ControlCommands.INIT;
import static com.github.rmohr.cockpitj.core.handler.ControlCommands.KILL;
import static com.github.rmohr.cockpitj.core.handler.ControlCommands.OPEN;
import static com.github.rmohr.cockpitj.core.handler.ControlCommands.PING;
import static com.github.rmohr.cockpitj.core.handler.ControlCommands.READY;

import java.util.HashMap;
import java.util.Map;

import javax.websocket.MessageHandler;

import com.github.rmohr.cockpitj.core.channel.ChannelReceiver;
import com.github.rmohr.cockpitj.core.channel.Message;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MultiplexingMessageHandler implements MessageHandler.Whole<String> {

    Map<String, ChannelReceiver> channels = new HashMap<>();
    Configuration config = Configuration.defaultConfiguration().addOptions(Option.DEFAULT_PATH_LEAF_TO_NULL);

    @Override
    public void onMessage(String s) {
        Message message = extractMessage(s);
        if (!message.isValid()) {
            log.warn("Invalid message received.");
        } else if (message.isControlCommand()) {
            switch (message.getCommand()) {
            case READY:
            case CLOSE:
            case DONE: {
                if (channels.containsKey(message.getChannel())) {
                    channels.get(message.getChannel()).onMessage(message);
                }
                break;
            }
            case KILL: {
                shutdown();
                break;
            }
            case PING:
            case OPEN:
            case INIT:
            default: {
                break;
            }
            }
        }

    }

    public void shutdown() {
        for (ChannelReceiver channelReceiver : channels.values()) {
            channelReceiver.onMessage(Message.builder()
                            .controlCommand(true)
                            .command(CLOSE)
                            .valid(true)
                            .channel(channelReceiver.getChannelId())
                            .build()
            );
        }
    }

    public void addChannel(ChannelReceiver channelReceiver) {
        channels.put(channelReceiver.getChannelId(), channelReceiver);
    }

    protected Message extractMessage(String message) {
        String[] splittedMessage = message.split("\n", 1);
        if (!message.contains("\n") || splittedMessage.length == 2 && splittedMessage[1].isEmpty()) {
            return Message.builder().body(message).valid(false).build();
        }
        if (splittedMessage.length == 1) {
            String command = JsonPath.using(config).parse(splittedMessage[0]).read("$.command");
            String channelId = "";
            if (ControlCommands.channelCommands.contains(channelId)) {
                channelId = JsonPath.using(config).parse(splittedMessage[0]).read("$.channel");
            }
            return Message.builder().channel(channelId).controlCommand(true).body(splittedMessage[0])
                    .command(command).valid(command == null).build();
        } else {
            String channelId = splittedMessage[0];
            String body = splittedMessage[1];

            return Message.builder().channel(channelId).controlCommand(false).body(body).valid(true).build();
        }
    }

}
