package com.github.rmohr.cockpitj.core.channel;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import com.github.rmohr.cockpitj.core.handler.ControlCommands;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QueuingChannelReceiver extends AbstractChannelReceiverImpl implements ChannelReceiver {

    private BlockingQueue<Message> queue;
    private String channelId;

    public QueuingChannelReceiver(String channelId, int capacity) {
        queue = new LinkedBlockingDeque<>(capacity);
        this.channelId = channelId;
    }

    public boolean waitForChannel(long duration, TimeUnit timeUnit) {
        try {
            Message message = queue.poll(duration, timeUnit);
            if (message == null) {
                return false;
            }
            if (message.isControlCommand()) {
                switch (message.getCommand()) {
                case ControlCommands.DONE:
                case ControlCommands.CLOSE: {
                    return false;
                }
                case ControlCommands.READY: {
                    return true;
                }
                }
            }
        } catch (InterruptedException e) {
            return false;
        }
        return false;
    }

    @Override
    public String getChannelId() {
        return channelId;
    }

    @Override
    public String pollMessage(long duration, TimeUnit timeUnit) {
        try {
            Message message = queue.poll(duration, timeUnit);
            if (message == null || message.isControlCommand()) {
                return null;
            } else {
                return message.getBody();
            }
        } catch (InterruptedException e) {
            log.debug("Interrupt {}", e);
        }
        return null;
    }

    @Override
    protected void doOnMessage(Message message) {
        try {
            queue.put(message);
        } catch (InterruptedException e) {
            log.debug("Interrupt {}", e);
        }
    }
}
