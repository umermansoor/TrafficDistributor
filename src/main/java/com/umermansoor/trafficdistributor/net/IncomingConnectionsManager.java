package com.umermansoor.trafficdistributor.net;

import com.umermansoor.trafficdistributor.config.Configuration;
import org.slf4j.Logger;

import java.net.ServerSocket;
import java.net.Socket;
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
    private final ExecutorService pool;
    private final CountDownLatch serverStartedSignal;
    private final Configuration config;
    private ServerSocket server;

    public IncomingConnectionsManager(CountDownLatch l, Configuration c) {
        serverStartedSignal = l;
        config = c;
        pool = Executors.newFixedThreadPool(c.MAX_CLIENT_CONNECTIONS);
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
            server = new ServerSocket(config.LISTENING_PORT);
        } catch (Exception e) {
            logger.error("failed to bind to port {}. {}", config.LISTENING_PORT, e.toString());
            return;
        }

        serverStartedSignal.countDown();

        logger.debug("listening on port {} for clients.", config.LISTENING_PORT);

        while (!Thread.currentThread().isInterrupted()) {
            try {

                Socket client = server.accept();
                logger.debug("new client connected {}.", client.getInetAddress().getHostAddress());
                // Pass the client socket to the handler thread.
                pool.submit(new IncomingConnection(client, config));
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
     * Normally to cancel a task(Runnable), we send an interrupt signal. But
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
