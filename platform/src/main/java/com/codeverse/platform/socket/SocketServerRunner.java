package com.codeverse.platform.socket;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/** Starts the TCP socket server on application startup (disabled in tests). */
@Component
@ConditionalOnProperty(name = "codeverse.socket.enabled", havingValue = "true", matchIfMissing = true)
public class SocketServerRunner implements CommandLineRunner {

    private final CodeVerseSocketServer socketServer;

    public SocketServerRunner(CodeVerseSocketServer socketServer) {
        this.socketServer = socketServer;
    }

    @Override
    public void run(String... args) {
        socketServer.start(9090);
        System.out.println("[Socket] CodeVerse socket server started on port 9090");
    }
}
