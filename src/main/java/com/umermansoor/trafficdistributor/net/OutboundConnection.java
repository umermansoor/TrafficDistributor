package com.umermansoor.trafficdistributor.net;

import com.umermansoor.trafficdistributor.collectors.EventCollector;
import com.umermansoor.trafficdistributor.config.Configuration;
import com.umermansoor.trafficdistributor.handlers.EventHandler;
import com.umermansoor.trafficdistributor.utils.Host;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;

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
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(OutboundConnection.class);
    private final Host host;
    private final EventCollector collector = Configuration.EVENTS_COLLECTOR;
    private final EventHandler eventHandler = EventHandler.getInstance();

    public OutboundConnection(Host h) {
        host = h;
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

    private void read(BufferedReader in) throws InterruptedException, IOException {
        while (!Thread.currentThread().isInterrupted()) {
            String json = in.readLine();
            if (json == null) {
                continue;
            }
            if (Configuration.TRACE_INCOMING_EVENTS) {
                logger.debug("received event: {}", json);
            }

            json = eventHandler.processEvent(json);
            // Skip the event is the handler returned null.
            if (json == null) {
                continue;
            }

            collector.put(json);
        }
    }
}
