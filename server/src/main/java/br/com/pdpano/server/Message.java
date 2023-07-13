package br.com.pdpano.server;

import java.time.Instant;

class Message {
    private String origin;
    private String interaction;
    private Long index;
    private Long created = Instant.now().getEpochSecond();

    public Message() {
    }

    public Message(String origin, String interaction) {
        this.origin = origin;
        this.interaction = interaction;
        this.index = 0L;
    }

    public Message(String origin, String interaction, Long index) {
        this.origin = origin;
        this.interaction = interaction;
        this.index = index;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getInteraction() {
        return interaction;
    }

    public void setInteraction(String interaction) {
        this.interaction = interaction;
    }

    public Long getIndex() {
        return index;
    }

    public void setIndex(Long index) {
        this.index = index;
    }

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    @Override
    public String toString() {
        return "Message{" +
                "origin='" + origin + '\'' +
                ", interaction='" + interaction + '\'' +
                ", index=" + index +
                ", created=" + created +
                '}';
    }
}
