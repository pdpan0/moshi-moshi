package br.com.pdpano.server;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@NoArgsConstructor
public class Chat {
    private final List<ClientRequester> clients = new ArrayList<>();
    private final Sinks.Many<String> messages = Sinks.many().multicast().onBackpressureBuffer();

    public void connect(ClientRequester client) {
        log.info("Client connected: {}", client);
        clients.add(client);
    }

    public void send(String message) {
        this.messages.tryEmitNext(message);
    }

    public Flux<String> getChat() {
        return this.messages.asFlux();
    }
}
