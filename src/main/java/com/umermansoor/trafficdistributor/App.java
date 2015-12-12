package com.umermansoor.trafficdistributor;

import com.umermansoor.trafficdistributor.config.Configuration;
import com.umermansoor.trafficdistributor.net.IncomingConnectionsManager;
import com.umermansoor.trafficdistributor.net.OutboundConnectionsManager;
import org.slf4j.Logger;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Application entry point. Starts other classes to run the app.
 *
 * @author umer mansoor
 */
public class App {

    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(App.class);
    private static final String name = "JSON TCP Distributor";
    private static final String version = "v0.1";


    public static void main(String[] args) {

        System.out.println(name + version);

        Configuration config = new Configuration();
        if (config.servers.length == 0) {
            logger.debug("no servers were found in the configuration to connect with. quitting");
            return;
        }

        CountDownLatch serverStartedSignal = new CountDownLatch(1);
        final IncomingConnectionsManager inboundConnectionManager = new
                IncomingConnectionsManager(serverStartedSignal, config);
        final Thread inboundThread = new Thread(inboundConnectionManager);
        inboundThread.start();

        // Wait for the internal TCP server to be started.
        try {
            boolean started = serverStartedSignal.await(5, TimeUnit.SECONDS);
            if (!started) {
                logger.debug("could not start internal server for accepting client connections. " +
                        "port already in use? quitting application");
                inboundThread.interrupt();
                inboundThread.join();
                return;
            }
        } catch (InterruptedException ignored) {
        }

        final OutboundConnectionsManager outboundConnectionManager = new
                OutboundConnectionsManager(config.servers, config.EVENTS_COLLECTOR,
                config.EVENTS_TRANSFORMER, config.CONNECTION_RETRY_FOREVER);
        final Thread outboundThread = new Thread(outboundConnectionManager);
        outboundThread.start();

        /**
         * Add a shutdown hook (CTRL+C, kill, etc.) to shutdown all other
         * threads gracefully and allow them to cleanup.
         */
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                logger.info("shutdown initiated");
                outboundThread.interrupt();
                inboundConnectionManager.cancelTask();

                try {
                    outboundThread.join();
                    inboundThread.join();
                } catch (InterruptedException ignored) {
                }

                logger.info("shutdown complete");
            }
        });
    }
}
