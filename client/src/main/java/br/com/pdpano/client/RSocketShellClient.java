package br.com.pdpano.client;

import io.rsocket.SocketAcceptor;
import lombok.extern.slf4j.Slf4j;
import org.jline.terminal.Terminal;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@ShellComponent
public class RSocketShellClient {
    private final String CLIENT = UUID.randomUUID().toString();
    private final RSocketRequester rSocketRequester;
    private final List<String> messages = new ArrayList<>();
    private final Terminal terminal;
    private Flux<String> chat;

    public RSocketShellClient(
            RSocketRequester.Builder rSocketRequester,
            RSocketStrategies strategies,
            Terminal terminal
    ) {
        this.terminal = terminal;

        log.info("Connecting using client ID: {}", CLIENT);

        SocketAcceptor responder = RSocketMessageHandler.responder(strategies, new ClientHandler());

        this.rSocketRequester = rSocketRequester
            .setupRoute("connect")
            .setupData(CLIENT)
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

    @ShellMethod(key = "clients", value = "Get all clients connected")
    public void clients() {
        this.rSocketRequester.route("clients")
                .retrieveFlux(String.class)
                .subscribe(it -> log.info("Clients requested: {}", it));
    }

    @ShellMethod(key = "chat", value = "Connects with chat.")
    public void chat() {
        this.rSocketRequester
                .route("chat")
                .retrieveFlux(String.class)
                .doOnNext(it -> this.terminal.writer().println(it))
                .subscribe();
    }

    @ShellMethod(key = "send", value = "Sent messages to chat")
    public void send(@ShellOption String message) {
        this.rSocketRequester.route("send")
                .data(message)
                .send()
                .doOnSuccess(it -> log.info("Message was sent successfully"))
                .subscribe();
    }

    @ShellMethod(key = "close", value = "Close chat")
    public void close() {
        this.rSocketRequester.route("close")
                .send()
                .subscribe();
    }

}
