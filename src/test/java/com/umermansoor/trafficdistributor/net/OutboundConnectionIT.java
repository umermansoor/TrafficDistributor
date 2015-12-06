package com.umermansoor.trafficdistributor.net;

import com.umermansoor.trafficdistributor.collectors.BlockingCollector;
import com.umermansoor.trafficdistributor.collectors.EventCollector;
import com.umermansoor.trafficdistributor.transformers.EventTransformer;
import com.umermansoor.trafficdistributor.utils.Host;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


public class OutboundConnectionIT {
    private final int mockServerPort = 3333;
    private Thread serverThread;

    @Before
    public void setUp() {
        // Start a server
        serverThread = new MockServer(mockServerPort);
        serverThread.start();
    }

    @Test
    public void testHost() {

        // Create an EventTransformer which returns the same event back
        // without any modification.
        final EventTransformer transformer = new EventTransformer() {

            @Override
            public String processEvent(String event) {
                return event;
            }
        };

        final EventCollector collector = new BlockingCollector(100);

        OutboundConnection oc = new OutboundConnection(new Host("localhost",
                mockServerPort), collector, transformer, 18);

        ExecutorService threadRunner = Executors.newSingleThreadExecutor();
        threadRunner.submit(oc, true);


        // Verify that all events were received in the collector
        for (String expected : MockServer.dataToSend) {
            String actual = null;

            try {
                actual = collector.get();
            } catch (Exception e) {
                fail("exception when retrieving data.");
            }

            assertEquals("data mismatch", expected, actual);
        }
        threadRunner.shutdownNow();
    }

    @After
    public void tearDown() {
        serverThread.interrupt();
        try {
            serverThread.join(500);
        } catch (Exception e) {
        }

    }
}
