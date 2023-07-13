package br.com.pdpano.client;

import io.rsocket.SocketAcceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import org.springframework.shell.standard.ShellComponent;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.UUID;

@Slf4j
@ShellComponent
public class RSocketShellClient {

    private final RSocketRequester rSocketRequester;

    @Autowired
    public RSocketShellClient(
        RSocketRequester.Builder rSocketRequester,
        RSocketStrategies strategies
    ) {
        final String client = UUID.randomUUID().toString();
        log.info("Connecting using client ID: {}", client);

        // (2)
        SocketAcceptor responder = RSocketMessageHandler.responder(strategies, new ClientHandler());

        this.rSocketRequester = rSocketRequester
                .setupRoute("shell-client")
                .setupData(client)
                .rsocketStrategies(strategies)
                .rsocketConnector(connector -> {
                    connector.acceptor(responder);
                    connector.reconnect(Retry.fixedDelay(2, Duration.ofSeconds(2)));
                })
                .connectTcp("localhost", 7000)
                .block();

        this.rSocketRequester.rsocket()
                .onClose()
                .doOnError(error -> log.warn("Connection CLOSED"))
                .doFinally(customer -> log.info("Client DISCONNECTED"))
                .subscribe();
    }
}
