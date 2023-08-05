package br.com.pdpano.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.annotation.ConnectMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
class ServerController {
    private final List<ClientRequester> clients = new ArrayList<>();
    private final Chat chat = new Chat();

    @ConnectMapping("connect")
    public void connect(RSocketRequester requester, @Payload String client) {
        requester.rsocketClient()
            .onClose()
            .doFirst(() -> {
                log.info("Client: {} CONNECTED.", client);
                clients.add(new ClientRequester(client, requester));
            })
            .doOnError(error -> log.warn("Channel to client {} CLOSED", client))
            .doFinally(consumer -> {
                clients.remove(new ClientRequester(client, requester));
                log.info("Client {} DISCONNECTED", client);
            })
            .subscribe();

        requester.route("client-status")
                .data("OPEN")
                .retrieveFlux(String.class)
//                .doOnNext(s -> log.info("Client: {} Free Memory: {}.",client,s))
                .subscribe();
    }

    @MessageMapping("clients")
    public Flux<String> clients(RSocketRequester requester) {
        return Flux.fromIterable(clients)
                .filter(it -> it.requester() != requester)
                .map(ClientRequester::client);
    }

    @MessageMapping("chat")
    public Flux<String> chat(RSocketRequester requester) {
        final ClientRequester client = this.clients.stream()
                .filter(it -> it.requester() == requester)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Client was not connected"));

        this.chat.connect(client);

        return this.chat.getChat();
    }

    @MessageMapping("send")
    public void send(String message) {
        this.chat.send(message);
    }

    @MessageMapping("close")
    public void close() {
        this.chat.close();
    }
}
