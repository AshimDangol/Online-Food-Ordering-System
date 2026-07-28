package com.library.model;

public class Notification {
    private String recipient;
    private String subject;
    private String message;
    private String channel;

    public Notification(String recipient, String subject, String message, String channel) {
        this.recipient = recipient;
        this.subject = subject;
        this.message = message;
        this.channel = channel;
    }

    public String getRecipient() { return recipient; }
    public String getSubject() { return subject; }
    public String getMessage() { return message; }
    public String getChannel() { return channel; }

    @Override
    public String toString() {
        return String.format("Notification[To=%s, Subject='%s', Channel=%s]", recipient, subject, channel);
    }
}
