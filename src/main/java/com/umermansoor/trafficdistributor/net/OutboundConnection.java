package com.umermansoor.trafficdistributor.net;

import com.umermansoor.trafficdistributor.collectors.EventCollector;
import com.umermansoor.trafficdistributor.config.TcpSocket;
import com.umermansoor.trafficdistributor.transformers.EventTransformer;
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
    private final EventCollector collector;
    private final EventTransformer eventTransformer;
    private final int readTimeInSeconds = TcpSocket.SOCKET_TIMEOUT_SECONDS;
    private Socket socket;

    public OutboundConnection(Host h, EventCollector cl, EventTransformer et) {
        host = h;
        collector = cl;
        eventTransformer = et;

    }

    public void run() {
        connect();
    }

    private void connect() {

        try {
            socket = new Socket(host.getHostname(), host.getPort());
            logger.debug("connected to {}", host);
            socket.setSoTimeout(readTimeInSeconds);

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
                logger.error("end of stream reached. breaking");
                break;
            }

            logger.trace("received event: {}", json);


            json = eventTransformer.processEvent(json);
            // Skip the event is the handler returned null.
            if (json == null) {
                continue;
            }

            collector.put(json);
        }
    }

}
