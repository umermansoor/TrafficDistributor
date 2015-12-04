package com.umermansoor.trafficdistributor.net;

import com.umermansoor.trafficdistributor.config.Configuration;
import com.umermansoor.trafficdistributor.utils.Host;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Manages Outbound TCP connections to servers.
 */
public class OutboundConnectionManager implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(OutboundConnectionManager.class);
    private final List<Host> hosts;
    private final Queue<String> centralQueue;

    public OutboundConnectionManager(List<Host> h, Queue<String> q) {
        hosts = h;
        centralQueue = q;
    }

    public void run() {
        ExecutorService pool = Executors.newFixedThreadPool(hosts.size());
        ExecutorCompletionService<Host> ecs = new ExecutorCompletionService<Host>(pool);

        for (Host host : hosts) {
            ecs.submit(new OutboundConnection(host, centralQueue), host);
        }

        while (!Thread.currentThread().isInterrupted()) {
            try {
                Host disconnected = ecs.take().get();
                logger.error("disconnected from {}.", disconnected.getHostname());

                if (Configuration.CONNECTION_RETRY_FOREVER) {
                    Thread.sleep(Configuration.CONNECTION_RETRY_DELAY_SECONDS);
                    ecs.submit(new OutboundConnection(disconnected, centralQueue), disconnected);
                }

            } catch (InterruptedException ie) {
                break;
            } catch (ExecutionException ee) {
                //TODO: Handle this
            }
        }

        pool.shutdownNow();

        boolean closed = false;
        try {
            closed = pool.awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS);
        } catch (InterruptedException ignored) { /** Ignored because exiting **/}

        if (closed) {
            logger.info("successfully shutdown OutboundConnectionManager.");
        } else {
            logger.error("failed to properly shutdown OutboundConnectionManager.");
        }
    }

}
