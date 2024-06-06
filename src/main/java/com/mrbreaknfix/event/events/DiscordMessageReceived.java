package com.mrbreaknfix.event.events;

import com.mrbreaknfix.event.Event;

public class DiscordMessageReceived extends Event {
    private String message;
    private String author;
    private String channelId;

    public DiscordMessageReceived(String message, String author, String channelId) {
        this.message = message;
        this.author = author;
        this.channelId = channelId;
    }

    public String getMessage() {
        return message;
    }

    public String getAuthor() {
        return author;
    }

    public String getChannelId() {
        return channelId;
    }
}
