package com.umermansoor.trafficdistributor.net;

import com.umermansoor.trafficdistributor.config.Configuration;
import com.umermansoor.trafficdistributor.utils.Host;
import org.slf4j.Logger;
import com.umermansoor.trafficdistributor.handlers.EventHandler;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Manages outgoing TCP connections to servers. For each server connection, it
 * creates a task which is an instance of {@link com.umermansoor.trafficdistributor.net.OutboundConnection}
 * and hands it the responsibility of handling communications with the server.
 *
 * If the socket to client becomes disconnected, it creates a new task to
 * re-establish connection (if specified in settings).
 *
 * @author umermansoor
 */
public class OutboundConnectionsManager implements Runnable {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(OutboundConnectionsManager.class);
    private final List<Host> hosts;
    private final BlockingQueue<String> centralQueue;
    private final EventHandler handler;

    public OutboundConnectionsManager(List<Host> h, BlockingQueue<String> q, EventHandler eh) {
        hosts = h;
        centralQueue = q;
        handler = eh;
    }

    public void run() {
        ExecutorService pool = Executors.newFixedThreadPool(hosts.size());
        ExecutorCompletionService<Host> ecs = new ExecutorCompletionService<Host>(pool);

        for (Host host : hosts) {
            ecs.submit(new OutboundConnection(host, centralQueue, handler), host);
        }

        while (!Thread.currentThread().isInterrupted()) {
            try {
                Host disconnected = ecs.take().get();
                logger.error("disconnected from {}.", disconnected.getHostname());

                if (Configuration.CONNECTION_RETRY_FOREVER) {
                    Thread.sleep(Configuration.CONNECTION_RETRY_DELAY_SECONDS);
                    ecs.submit(new OutboundConnection(disconnected, centralQueue, handler), disconnected);
                }

            } catch (InterruptedException ie) {
                break;
            } catch (ExecutionException ee) {
                //TODO: Handle this
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
            logger.info("successfully shutdown OutboundConnectionsManager.");
        } else {
            logger.error("failed to properly shutdown OutboundConnectionsManager.");
        }
    }

}
