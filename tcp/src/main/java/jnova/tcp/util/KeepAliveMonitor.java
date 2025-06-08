package jnova.tcp.util;

import jnova.tcp.TcpSession;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.*;

public class KeepAliveMonitor {
    private final Map<String, TcpSession> sessionMap;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final Duration timeout;

    public KeepAliveMonitor(Map<String, TcpSession> sessionMap, Duration timeout) {
        this.sessionMap = sessionMap;
        this.timeout = timeout;
    }

    public void start() {
        scheduler.scheduleAtFixedRate(this::checkSessions, 0, 10, TimeUnit.SECONDS);
    }

    public void stop() {
        scheduler.shutdownNow();
    }

    private void checkSessions() {
        long now = System.currentTimeMillis();
        for (TcpSession session : sessionMap.values()) {
            if (!session.isAlive()) continue;
            if (now - session.getLastKeepAlive() > timeout.toMillis()) {
                System.out.println("Keep-alive timeout for session: " + session.getId());
                try {
                    session.send("KEEPALIVE_TIMEOUT\n".getBytes()).subscribe();
                    session.close();
                } catch (IOException e) {
                    System.err.println("Error closing session " + session.getId() + ": " + e.getMessage());
                }
            }
        }
    }
}
