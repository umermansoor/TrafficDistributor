package com.umermansoor.trafficdistributor.net;

import com.umermansoor.trafficdistributor.Configuration;
import org.slf4j.Logger;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Handles incoming connections.
 */
public class IncomingConnectionManager implements Runnable {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(IncomingConnectionManager.class);
    private final Queue<String> centralQueue;
    private final ExecutorService pool = Executors.newFixedThreadPool(Configuration.MAX_CLIENT_CONNECTIONS);
    ServerSocket server;

    public IncomingConnectionManager(Queue<String> q) {
        centralQueue = q;

    }

    public void run() {
        startServer();

    }

    private void startServer() {

        try {
            server = new ServerSocket(Configuration.LISTENING_PORT);
        } catch (Exception e) {
            logger.error("failed to bind to port {}. {}", Configuration.LISTENING_PORT, e.toString());
            return;
        }

        logger.debug("listening on port {} for clients.", Configuration.LISTENING_PORT);

        while (!Thread.currentThread().isInterrupted()) {
            try {

                Socket client = server.accept();
                pool.submit(new IncomingConnection(centralQueue, client));
                logger.debug("client connected {}.", client.getInetAddress().getHostAddress());
            } catch (java.io.IOException ioe) {
                logger.error("{}", ioe.toString());
                break;
            }
        }

        pool.shutdownNow();

        boolean closed = false;
        try {
            closed = pool.awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS);
        } catch (InterruptedException ignored) { /** Ignored because exiting **/}

        if (closed) {
            logger.info("successfully shutdown IncomingConnectionManager.");
        } else {
            logger.error("failed to properly shutdown IncomingConnectionManager.");
        }
    }

    public synchronized void cancelTask() {
        if (server != null) {
            try {
                logger.debug("closing socket to terminate socket.accept() loop...");
                server.close();
            } catch (java.io.IOException ignored) {
                logger.error("unable to close server socket. {}", ignored.toString());
            }
        }
    }

}
