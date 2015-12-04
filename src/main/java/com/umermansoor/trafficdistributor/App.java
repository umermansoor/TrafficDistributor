package com.umermansoor.trafficdistributor;

import com.umermansoor.trafficdistributor.net.IncomingConnectionsManager;
import com.umermansoor.trafficdistributor.net.OutboundConnectionsManager;
import com.umermansoor.trafficdistributor.utils.Host;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import com.umermansoor.trafficdistributor.handlers.EventHandler;

/**
 * Application entry point. Starts other classes.
 *
 * @author umer mansoor
 */
public class App {

    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        ArrayList<Host> hosts = new ArrayList<Host>(1);
        hosts.add(new Host("localhost", 6001));

        /**
         * The central queue where events received from servers are stored and
         * are consumed by client tasks.
         */
        LinkedBlockingQueue<String> centralQueue = new LinkedBlockingQueue<String>(2000);

        EventHandler noOpEventHandler = new EventHandler() {

            /**
             * Do nothing; Return the event.
             *
             * @param event
             * @return event.
             */
            public String processEvent(String event) {
                return event;
            }

        };

        CountDownLatch serverStartedSignal = new CountDownLatch(1);
        final IncomingConnectionsManager inboundConnectionManager = new IncomingConnectionsManager(
                centralQueue, serverStartedSignal, noOpEventHandler);
        final Thread inboundThread = new Thread(inboundConnectionManager);
        inboundThread.start();

        // Wait for the internal TCP server to be started.
        try {
            boolean started = serverStartedSignal.await(5, TimeUnit.SECONDS);
            if (!started) {
                logger.debug("could not start internal server for accepting client connections. " +
                        " Port already in use? Quitting application.");
                inboundThread.interrupt();
                inboundThread.join();
                return;
            }
        } catch (InterruptedException ignored) {
        }

        final OutboundConnectionsManager outboundConnectionManager = new OutboundConnectionsManager(
                hosts, centralQueue, noOpEventHandler);
        final Thread outboundThread = new Thread(outboundConnectionManager);
        outboundThread.start();

        /**
         * Add a shutdown hook (CTRL+C, kill, etc.) to shutdown all other
         * threads gracefully and allow them to cleanup.
         */
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                logger.info("shutdown initiated.");
                outboundThread.interrupt();
                inboundConnectionManager.cancelTask();

                try {
                    outboundThread.join();
                    inboundThread.join();
                } catch (InterruptedException ignored) {
                }

                logger.info("shutdown completed. good bye.");
            }
        });
    }
}
