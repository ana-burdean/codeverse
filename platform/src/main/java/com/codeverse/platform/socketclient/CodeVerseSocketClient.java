package com.codeverse.platform.socketclient;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Simple console client to talk to the CodeVerseSocketServer.
 * Run after starting Spring Boot:
 *   mvn -q -DskipTests spring-boot:run
 * Then in another terminal:
 *   mvn -q -DskipTests -Dexec.mainClass=com.codeverse.platform.socketclient.CodeVerseSocketClient exec:java
 *
 * Or run this class from your IDE.
 */
public class CodeVerseSocketClient {

    public static void main(String[] args) throws Exception {
        String host = args.length > 0 ? args[0] : "127.0.0.1";
        int port = args.length > 1 ? Integer.parseInt(args[1]) : 9090;

        try (Socket socket = new Socket(host, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
             BufferedReader console = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8))) {

            // print greeting
            for (int i = 0; i < 2; i++) System.out.println(in.readLine());

            while (true) {
                System.out.print("> ");
                String cmd = console.readLine();
                if (cmd == null) break;

                out.write(cmd + "\n");
                out.flush();

                // Read response until END / BYE / PONG / ERR or a single line
                String line;
                while ((line = in.readLine()) != null) {
                    System.out.println(line);
                    if (line.equals("END") || line.equals("BYE") || line.equals("PONG") || line.startsWith("ERR") || line.startsWith("WELCOME")) {
                        break;
                    }
                }
                if ("QUIT".equalsIgnoreCase(cmd.trim())) break;
            }
        }
    }
}