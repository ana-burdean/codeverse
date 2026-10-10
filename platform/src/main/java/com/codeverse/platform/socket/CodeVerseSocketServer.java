package com.codeverse.platform.socket;

import com.codeverse.platform.model.Course;
import com.codeverse.platform.service.CourseService;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *   PING -> PONG
 *   COURSES -> one course per line: <id>|<title> then END
 *   QUIT -> closes connection
 */

@Component
public class CodeVerseSocketServer {

    private final CourseService courseService;

    private final ExecutorService pool = Executors.newFixedThreadPool(8);
    private volatile boolean running = false;

    public CodeVerseSocketServer(CourseService courseService) {
        this.courseService = courseService;
    }

    public void start(int port) {
        if (running) return;
        running = true;
        Thread acceptThread = new Thread(() -> runServer(port), "codeverse-socket-accept");
        acceptThread.setDaemon(true);
        acceptThread.start();
    }

    private void runServer(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (running) {
                Socket client = serverSocket.accept();
                pool.submit(() -> handleClient(client));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClient(Socket client) {
        try (client;
             BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream(), StandardCharsets.UTF_8));
             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream(), StandardCharsets.UTF_8))) {

            out.write("WELCOME CodeVerse Socket Server\n");
            out.write("COMMANDS: PING, COURSES, QUIT\n");
            out.flush();

            String line;
            while ((line = in.readLine()) != null) {
                String cmd = line.trim().toUpperCase();
                if (cmd.isEmpty()) continue;

                switch (cmd) {
                    case "PING" -> {
                        out.write("PONG\n");
                        out.flush();
                    }
                    case "COURSES" -> {
                        List<Course> courses = courseService.getAllCourses();
                        for (Course c : courses) {
                            out.write(c.getId() + "|" + c.getTitle() + "\n");
                        }
                        out.write("END\n");
                        out.flush();
                    }
                    case "QUIT" -> {
                        out.write("BYE\n");
                        out.flush();
                        return;
                    }
                    default -> {
                        out.write("ERR Unknown command\n");
                        out.flush();
                    }
                }
            }
        } catch (Exception e) {
        }
    }
}