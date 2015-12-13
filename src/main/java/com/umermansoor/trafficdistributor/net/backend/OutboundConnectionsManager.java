package com.umermansoor.trafficdistributor.net.backend;

import com.umermansoor.trafficdistributor.collectors.EventCollector;
import com.umermansoor.trafficdistributor.transformers.EventTransformer;
import com.umermansoor.trafficdistributor.utils.Host;
import org.slf4j.Logger;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Manages outgoing TCP connections to servers. For each server connection, it
 * creates a task which is an instance of {@link OutboundConnection}
 * and hands it the responsibility of handling communications with the server.
 *
 * If the socket to client becomes disconnected, it creates a new task to
 * re-establish connection (if specified in settings).
 *
 * @author umermansoor
 */
public class OutboundConnectionsManager implements Runnable {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(OutboundConnectionsManager.class);
    private final Host[] hosts;
    private final EventCollector collector;
    private final EventTransformer transformer;
    private final boolean retryForever;

    public OutboundConnectionsManager(Host[] h, EventCollector cl,
                                      EventTransformer et, boolean retry) {
        hosts = h;
        collector = cl;
        transformer = et;
        retryForever = retry;
    }

    public void run() {
        ExecutorService pool = Executors.newFixedThreadPool(hosts.length);
        ExecutorCompletionService<Host> ecs = new ExecutorCompletionService<Host>(pool);

        for (Host host : hosts) {
            ecs.submit(new OutboundConnection(host, collector, transformer), host);
        }

        // Shutdown the pool once all outbound connections tasks are finished
        // if we are not required to retry connection upon disconnect. This
        // will prevent this thread from running forever when no tasks are
        // running or could be run.
        if (!retryForever) {
            pool.shutdown();
        }

        while (!Thread.currentThread().isInterrupted() && !pool.isTerminated()) {

            try {
                Host disconnected = ecs.take().get();

                logger.error("disconnected from {}.", disconnected.getHostname());

                if (retryForever) {
                    Thread.sleep(1000); // Wait this number of seconds before retrying
                    ecs.submit(new OutboundConnection(disconnected, collector, transformer), disconnected);
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
