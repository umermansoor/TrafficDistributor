package com.umermansoor.trafficdistributor.net;

import org.slf4j.Logger;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Queue;

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
    private final Queue<String> centralEventsQueue;
    private final Socket clientSocket;

    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(IncomingConnection.class);


    public IncomingConnection(Queue<String> q, Socket s) {
        centralEventsQueue = q;
        clientSocket = s;
    }

    public void run() {
        String event;
        while (!Thread.currentThread().isInterrupted()) {
            event = centralEventsQueue.poll();
            if (event == null) {
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
                out.write(event);
                out.newLine();
                out.flush();
            } catch (IOException ioe) {
                logger.error("ioexception sending event to {}. {}",
                        clientSocket.getInetAddress().getHostAddress(), ioe.toString());
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
