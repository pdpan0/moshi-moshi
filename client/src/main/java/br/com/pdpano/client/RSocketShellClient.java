package br.com.pdpano.client;

import io.rsocket.SocketAcceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import reactor.util.retry.Retry;

import java.util.UUID;

@Slf4j
@ShellComponent
public class RSocketShellClient {

    private final RSocketRequester rSocketRequester;
    private final String client = UUID.randomUUID().toString();

    public RSocketShellClient(
            RSocketRequester.Builder rSocketRequester,
            RSocketStrategies strategies
    ) {
        log.info("Connecting using client ID: {}", client);

        SocketAcceptor responder = RSocketMessageHandler.responder(strategies, new ClientHandler());

        this.rSocketRequester = rSocketRequester
            .setupRoute("connect")
            .setupData(client)
            .rsocketStrategies(strategies)
            .rsocketConnector(connector -> {
                connector.acceptor(responder);
                connector.reconnect(Retry.indefinitely());
            })
            .tcp("localhost", 7000);

        this.rSocketRequester.rsocketClient()
                .onClose()
                .doOnError(error -> log.warn("Connection CLOSED"))
                .doFinally(customer -> log.info("Client DISCONNECTED"))
                .subscribe();

        this.rSocketRequester.rsocketClient().connect();
    }

    @ShellMethod("clients")
    public void clients() {
        this.rSocketRequester.route("clients")
                .retrieveFlux(String.class)
                .subscribe(it -> log.info("Clients requested: {}", it));
    }

    @ShellMethod("chat")
    public void chat() {
        this.rSocketRequester.route("chat")
                .retrieveFlux(String.class)
                .subscribe(it -> log.info("Getting chat: {}", it));
    }

    @ShellMethod("send")
    public void send(@ShellOption String message) {
        this.rSocketRequester.route("send")
                .data(message)
                .send()
                .subscribe();
    }

}
