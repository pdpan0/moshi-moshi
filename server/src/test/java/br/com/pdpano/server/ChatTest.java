package br.com.pdpano.server;

import org.junit.jupiter.api.Test;
import reactor.core.Disposable;

import static org.junit.jupiter.api.Assertions.*;

class ChatTest {

    @Test
    void chat_should_notify_clients() {
        final Chat chat = new Chat();

        final Disposable firstClient = chat.getChat().doOnNext(it -> System.out.println("[CLIENT 1] " + it)).subscribe();
        final Disposable secondClient = chat.getChat().doOnNext(it -> System.out.println("[CLIENT 2] " + it)).subscribe();

        chat.send("Teste 1");
        chat.send("Teste 2");
        chat.send("Teste 3");
        chat.send("Teste 4");

    }
}