package com.github.rmohr.cockpitj.core.channel;

import java.util.concurrent.TimeUnit;

public interface ChannelReceiver {

    String getChannelId();

    void onMessage(Message message);

    boolean isOpen();

    boolean isDone();

    String pollMessage(long duration, TimeUnit timeUnit);

}
