package com.umermansoor.trafficdistributor.net;

import com.umermansoor.trafficdistributor.config.Configuration;
import com.umermansoor.trafficdistributor.utils.Host;
import org.slf4j.Logger;
import com.umermansoor.trafficdistributor.handlers.EventHandler;
import java.io.BufferedReader;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Task for handling communication with an outbound socket. The socket
 * represents a server connection from which data is received. This task
 * will run until it is interrupted by another thread.
 *
 * When an event is received from the server, it stores the event to the
 * central queue.
 *
 * @author umermansoor
 */
public class OutboundConnection implements Runnable {
    private final Host host;
    private final BlockingQueue<String> centralQueue;
    private final EventHandler eventHandler;

    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(OutboundConnection.class);

    public OutboundConnection(Host h, BlockingQueue<String> q, EventHandler eh) {
        host = h;
        centralQueue = q;
        eventHandler = eh;
    }

    public void run() {
        connect();
    }

    private void connect() {
        Socket socket = null;
        try {
            socket = new Socket(host.getHostname(), host.getPort());
            logger.debug("connected to {}", host);
            socket.setSoTimeout(Configuration.SOCKET_TIMEOUT_SECONDS);

            read(new BufferedReader(new java.io.InputStreamReader(socket.getInputStream())));
        } catch (Exception e) {
              // Log the exception and give up.
            logger.error("error communicating with host (server) {}. {}", host, e.toString());
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (java.io.IOException ioe) {
                    logger.error("host {} socket not properly closed. {}",
                            host, ioe.toString());
                }
            }
        }
    }

    private void read(BufferedReader in) {
        String json;

        try {
            while ((json = in.readLine()) != null || !Thread.currentThread().isInterrupted()) {
                if (Configuration.TRACE_INCOMING_EVENTS) {
                    logger.debug("received event: {}", json);
                }

                json = eventHandler.processEvent(json);
                // Skip the event is the handler returned null.
                if (json == null) {
                    continue;
                }

                if (!centralQueue.offer(json, 1, TimeUnit.SECONDS)) {
                    logger.error("queue full. no or slow clients connected? dropped event {}.", json);
                }
            }
        } catch (Exception e) {
            // Log and give up
            logger.debug("error reading from {}. {}", host, e.toString());
        }

    }
}
