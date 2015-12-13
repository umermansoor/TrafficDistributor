package com.umermansoor.trafficdistributor.net.frontend;

import com.umermansoor.trafficdistributor.collectors.EventCollector;
import com.umermansoor.trafficdistributor.config.Configuration;
import com.umermansoor.trafficdistributor.transformers.EventTransformer;
import org.slf4j.Logger;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * Task for handling communication with an incoming socket. The socket
 * represents a client connection to which data is to be sent. This task
 * will run until it is interrupted by another thread.
 *
 * It checks the central queue for events and if an event is available, it
 * removes it from the queue and sends it to the client.
 *
 * @author umermansoor
 */
public class IncomingConnection implements Runnable {

    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(IncomingConnection.class);
    private final Socket clientSocket;
    private final EventTransformer eventTransformer;
    private final EventCollector collector;

    public IncomingConnection(Socket s, Configuration c) {
        clientSocket = s;
        collector = c.EVENTS_COLLECTOR;
        eventTransformer = c.EVENTS_TRANSFORMER;
    }

    public void run() {
        String json;
        while (!Thread.currentThread().isInterrupted()) {
            try {
                json = collector.get();
            } catch (InterruptedException ie) {
                break;
            }

            if (json == null) {
                continue;
            }

            /**
             * Note: In a normal Mission Critical application, implementing a
             * PING-PONG (or Heartbeat) mechanism is highly recommended to
             * avoid the loss of data.
             */

            try {
                BufferedWriter out = new BufferedWriter(new
                        OutputStreamWriter(clientSocket.getOutputStream()));

                json = eventTransformer.processEvent(json);

                if (json != null) {
                    out.write(json);
                    out.newLine();
                    out.flush();
                }
            } catch (IOException ioe) {
                logger.error("exception sending json to {}. {}",
                        clientSocket.getInetAddress().getHostAddress(), ioe.toString());
                break; // Fail-fast. Clients should reconnect.
            }
        }

        try {
            clientSocket.close();
        } catch (IOException ioe) {
            logger.error("ioexception closing client socket {}. {}",
                    clientSocket.getInetAddress().getHostAddress(), ioe.toString());
        }
    }

}
