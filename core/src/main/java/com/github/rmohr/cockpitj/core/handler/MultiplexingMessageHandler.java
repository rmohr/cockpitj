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
                } else {
                    log.warn("Message for not registerd channel '{}' received", message.getChannel());
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
        } else {
            if (channels.containsKey(message.getChannel())) {
                channels.get(message.getChannel()).onMessage(message);
            } else {
                log.warn("Message for not registered channel '{}' received", message.getChannel());
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
        String[] splittedMessage = message.split("\\n", 2);
        if (!message.contains("\n") || splittedMessage[1].isEmpty()) {
            return Message.builder().body(message).valid(false).build();
        }
        if (splittedMessage[0].isEmpty()) {
            String command = JsonPath.using(config).parse(splittedMessage[1]).read("$.command");
            String channelId = JsonPath.using(config).parse(splittedMessage[1]).read("$.channel");
            return Message.builder().channel(channelId != null ? channelId : "").controlCommand(true).body
                    (splittedMessage[1])
                    .command(command).valid(command != null).build();
        } else {
            String channelId = splittedMessage[0];
            String body = splittedMessage[1];

            return Message.builder().channel(channelId).controlCommand(false).body(body).valid(true).build();
        }
    }

}
