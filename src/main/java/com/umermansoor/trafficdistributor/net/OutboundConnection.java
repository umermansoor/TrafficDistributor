package com.umermansoor.trafficdistributor.net;

import com.umermansoor.trafficdistributor.Configuration;
import com.umermansoor.trafficdistributor.util.Host;
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
        try {
            Socket socket = new Socket(host.getHostname(), host.getPort());
            logger.debug("connected to {}", host);
            socket.setSoTimeout(Configuration.SOCKET_TIMEOUT);

            read(new BufferedReader(new InputStreamReader(socket.getInputStream())));
        } catch (Exception e) {
            /**
             * Log the exception and give up.
             */
            logger.error("Error connection to host {}.", host, e.toString());
        }
    }

    private void read(BufferedReader in) {
        String json;

        try {
            while ((json = in.readLine()) != null) {
                logger.debug("received event: {}", json);
            }
        } catch (Exception e) {
            // Log and give up
            logger.debug("Error reading from {}.", host, e.toString());
        }

    }
}
