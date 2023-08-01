package br.com.pdpano.server;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
class Message {
    private String origin;
    private String interaction;
    private String destination;
    private Long index;
    private Long created = Instant.now().getEpochSecond();

    public Message(String origin, String interaction, String destination) {
        this.origin = origin;
        this.interaction = interaction;
        this.destination = destination;
        this.index = 0L;
    }

    public Message(String origin, String interaction, String destination, Long index) {
        this.origin = origin;
        this.interaction = interaction;
        this.destination = destination;
        this.index = index;
    }
}

