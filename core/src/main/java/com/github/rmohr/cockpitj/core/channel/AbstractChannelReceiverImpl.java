package com.github.rmohr.cockpitj.core.channel;

import static com.github.rmohr.cockpitj.core.handler.ControlCommands.CLOSE;
import static com.github.rmohr.cockpitj.core.handler.ControlCommands.DONE;
import static com.github.rmohr.cockpitj.core.handler.ControlCommands.OPEN;

public abstract class AbstractChannelReceiverImpl implements ChannelReceiver {

    private boolean open;
    private boolean done;

    protected abstract void doOnMessage(Message message);

    @Override
    public final void onMessage(Message message) {
        if (!message.isValid()) {
            throw new RuntimeException("Invalid messages should never reach this point");
        }
        if (message.isControlCommand()) {
            switch (message.getCommand()) {
            case OPEN: {
                open = true;
                break;
            }
            case CLOSE: {
                open = false;
                done = true;
                break;
            }
            case DONE: {
                done = true;
            }
            default:
                break;
            }
        }
        doOnMessage(message);
    }

    @Override
    public boolean isOpen() {
        return open;
    }

    @Override
    public boolean isDone() {
        return done;
    }
}
