package com.umermansoor.trafficdistributor;

import com.umermansoor.trafficdistributor.net.IncomingConnectionManager;
import com.umermansoor.trafficdistributor.net.OutboundConnectionManager;
import com.umermansoor.trafficdistributor.util.Host;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

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
        LinkedBlockingQueue<String> centralQueue = new LinkedBlockingQueue<String>(2000);

        CountDownLatch serverStartedSignal = new CountDownLatch(1);
        final IncomingConnectionManager inboundconnectionManager = new IncomingConnectionManager(
                centralQueue, serverStartedSignal);
        final Thread inboundThread = new Thread(inboundconnectionManager);
        inboundThread.start();

        try {
            boolean started = serverStartedSignal.await(5, TimeUnit.SECONDS);
            if (started == false) {
                logger.debug("could not start server to accept client connections. Quitting application.");
                inboundThread.interrupt();
                inboundThread.join();
                return;
            }
        } catch (InterruptedException ignored) {
        }

        final OutboundConnectionManager outboundConnectionManager = new OutboundConnectionManager(
                hosts, centralQueue);
        final Thread outboundThread = new Thread(outboundConnectionManager);
        outboundThread.start();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                logger.info("shutdown initiated.");
                outboundThread.interrupt();
                inboundconnectionManager.cancelTask();

                try {
                    outboundThread.join();
                    inboundThread.join();
                } catch (InterruptedException ignored) {
                }

                logger.info("shutdown completed.");
            }
        });

    }
}
