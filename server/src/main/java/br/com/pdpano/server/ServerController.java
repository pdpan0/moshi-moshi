package br.com.pdpano.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.annotation.ConnectMapping;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
class ServerController {
    private final List<RSocketRequester> CLIENTS = new ArrayList<>();

    @ConnectMapping("shell-client")
    void connectShellClientAndAskForTelemetry(RSocketRequester requester, @Payload String client) {
        requester.rsocket()
            .onClose()
            .doFirst(() -> {
                log.info("Client: {} CONNECTED.", client);
                CLIENTS.add(requester);
            })
            .doOnError(error -> {
                log.warn("Channel to client {} CLOSED", client);
            })
            .doFinally(consumer -> {
                CLIENTS.remove(requester);
                log.info("Client {} DISCONNECTED", client);
            })
            .subscribe();

        requester.route("client-status")
                .data("OPEN")
                .retrieveFlux(String.class)
                .doOnNext(s -> log.info("Client: {} Free Memory: {}.",client,s))
                .subscribe();
    }

}
