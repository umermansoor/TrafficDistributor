package com.umermansoor.trafficdistributor.net;

import org.slf4j.Logger;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Queue;

/**
 * Handles TCP Client connection.
 */
public class IncomingConnection implements Runnable {

    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(IncomingConnection.class);
    private final Queue<String> centralQueue;
    private final Socket clientSocket;


    public IncomingConnection(Queue<String> q, Socket s) {
        centralQueue = q;
        clientSocket = s;
    }

    public void run() {
        String event;
        while (!Thread.currentThread().isInterrupted()) {
            event = centralQueue.poll();
            if (event == null) {
                continue;
            }

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
