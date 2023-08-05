package br.com.pdpano.server;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Chat {
    private final List<ClientRequester> clients = new ArrayList<>();
    private final Flux<String> messages;
    private FluxSink<String> sink;
//    private final Sinks.Many<String> messages = Sinks.many().multicast().onBackpressureBuffer();

    public Chat() {
        this.messages = Flux.<String>create(sink1 -> this.sink = sink1).share();
    }

    public void connect(ClientRequester client) {
        log.info("Client connected: {}", client);
        clients.add(client);
    }

    public void send(String message) {
        this.sink.next(message);
    }

    public Flux<String> getChat() {
//        return this.messages.asFlux().doOnNext(it -> log.info("Message received: {}", it));
        return this.messages;
    }

    public void close() {
        this.sink.complete();
    }
}
