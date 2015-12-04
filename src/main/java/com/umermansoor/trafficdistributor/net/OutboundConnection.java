package com.umermansoor.trafficdistributor.net;

import com.umermansoor.trafficdistributor.config.Configuration;
import com.umermansoor.trafficdistributor.utils.Host;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Queue;

/**
 * Connects to a server that is producing events and puts events in the
 * cental queue.
 *
 * @author umermansoor
 */
public class OutboundConnection implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(OutboundConnection.class);
    private final Host host;
    private final Queue<String> centralQueue;

    public OutboundConnection(Host h, Queue<String> q) {
        this.host = h;
        this.centralQueue = q;
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

            read(new BufferedReader(new InputStreamReader(socket.getInputStream())));
        } catch (Exception e) {
            /**
             * Log the exception and give up.
             */
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
            while ((json = in.readLine()) != null ||
                    !Thread.currentThread().isInterrupted()) {
                logger.debug("received event: {}", json);
                centralQueue.add(json);
            }
        } catch (Exception e) {
            // Log and give up
            logger.debug("error reading from {}. {}", host, e.toString());
        }

    }
}
