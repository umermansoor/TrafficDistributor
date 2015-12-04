package com.umermansoor.trafficdistributor.net;

import com.umermansoor.trafficdistributor.config.Configuration;
import org.slf4j.Logger;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Manages incoming TCP connections from clients. Starts a TCP server to
 * listen for client connections. When a client is connected, it creates a new
 * instance of {@link com.umermansoor.trafficdistributor.net.IncomingConnection} and hands it the
 * responsibility of handling communications with this client.
 *
 * @author umermansoor
 */
public class IncomingConnectionsManager implements Runnable {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(IncomingConnectionsManager.class);
    private final Queue<String> centralQueue;
    private final ExecutorService pool = Executors.newFixedThreadPool(Configuration.MAX_CLIENT_CONNECTIONS);
    private final CountDownLatch serverStartedSignal;
    ServerSocket server;

    public IncomingConnectionsManager(Queue<String> q, CountDownLatch l) {
        centralQueue = q;
        serverStartedSignal = l;

    }

    public void run() {
        startServer();
    }

    /**
     * Starts a TCP server to listen for clients. Exits if the server couldn't
     * be started.
     */
    private void startServer() {
        try {
            server = new ServerSocket(Configuration.LISTENING_PORT);
        } catch (Exception e) {
            logger.error("failed to bind to port {}. {}", Configuration.LISTENING_PORT, e.toString());
            return;
        }

        serverStartedSignal.countDown();

        logger.debug("listening on port {} for clients.", Configuration.LISTENING_PORT);

        while (!Thread.currentThread().isInterrupted()) {
            try {

                Socket client = server.accept();
                logger.debug("new client connected {}.", client.getInetAddress().getHostAddress());
                // Pass the client socket to the handler thread.
                pool.submit(new IncomingConnection(centralQueue, client));
            } catch (java.io.IOException ioe) {
                logger.error("{}", ioe.toString());
                break;
            }
        }

        /**
         * In a real application, this is where some type of cleanup will be
         * performed.
         */

        pool.shutdownNow();

        boolean closed = false;
        try {
            closed = pool.awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS);
        } catch (InterruptedException ignored) { /** Ignored because exiting **/}

        if (closed) {
            logger.info("successfully shutdown IncomingConnectionsManager.");
        } else {
            logger.error("failed to properly shutdown IncomingConnectionsManager.");
        }
    }

    /**
     * Normally to cancel a task(Runnable), we send am interrupt signal. But
     * the call to `ServerSocket.accept()` ignores interrupt and blocks until a
     * connection request is received. As a work around, this method closes the
     * underlying `Socket` forcing the `accept()` method to throw an exception,
     * giving the current thread a chance to process the interrupt request.
     */
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
