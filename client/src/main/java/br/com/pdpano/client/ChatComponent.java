package br.com.pdpano.client;

import org.jline.terminal.Terminal;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
public class ChatComponent {
    private final Terminal terminal;

    public ChatComponent(Terminal terminal) {
        this.terminal = terminal;
    }

    @ShellMethod("Let's chatting")
    public void interact() {
        terminal.writer().println("oiii");
    }
}