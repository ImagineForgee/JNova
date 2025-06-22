package jnova.tcp.util;

import jnova.tcp.TcpSession;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Monitors TCP sessions and closes them if they exceed a specified keep-alive timeout.
 *
 * This class uses a scheduled executor to periodically check the last keep-alive timestamp
 * of each session. If a session hasn't sent a keep-alive signal within the configured
 * timeout, it's considered inactive and will be closed.  A "KEEPALIVE_TIMEOUT" message
 * is sent to the session before closing.
 */
public class KeepAliveMonitor {
    private final Map<String, TcpSession> sessionMap;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final Duration timeout;

        /**
     * Constructs a KeepAliveMonitor with the specified session map and timeout duration.
     *
     * @param sessionMap The map containing session IDs and their corresponding TcpSession objects.
     * @param timeout The duration after which a session is considered inactive if no activity is detected.
     */
    public KeepAliveMonitor(Map<String, TcpSession> sessionMap, Duration timeout) {
        this.sessionMap = sessionMap;
        this.timeout = timeout;
    }

        /**
     * Starts the session checking scheduler.
     *
     * This method schedules the {@link #checkSessions()} method to run at a fixed rate,
     * starting immediately and repeating every 10 seconds.
     */
    public void start() {
        scheduler.scheduleAtFixedRate(this::checkSessions, 0, 10, TimeUnit.SECONDS);
    }

        /**
     * Stops the scheduler, preventing it from executing any further tasks.
     *
     * This method attempts to halt all actively executing tasks and immediately shuts
     * down the scheduler's execution service.  Pending tasks will not be executed.
     */
    public void stop() {
        scheduler.shutdownNow();
    }

        /**
     * Checks for inactive TCP sessions and closes them if a keep-alive timeout has occurred.
     *
     * This method iterates through all active sessions, verifies their liveness, and checks if the time elapsed
     * since the last keep-alive signal exceeds the configured timeout duration. If a timeout is detected, a
     * "KEEPALIVE_TIMEOUT" message is sent to the session, and the session is closed. Any exceptions during
     * session closure are caught and logged.
     */
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
