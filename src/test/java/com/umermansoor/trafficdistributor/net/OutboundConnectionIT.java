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

/**
 * Integrations Tests for {@link OutboundConnection}.
 * It uses {@link MockServer} to start a mock TCP server.
 *
 * @author umermansoor
 */
public class OutboundConnectionIT {
    private final int port = 3333;
    MockServer mockServer;

    @Before
    public void setUp() {
        // Start a server
        mockServer = new MockServer(port);
        mockServer.start();
    }

    @Test
    public void connectWithMockServer_VerifyEventsAreReceived() {
        // Create an EventTransformer which returns the same event back without
        // modifications.
        final EventTransformer transformer = new EventTransformer() {

            @Override
            public String processEvent(String event) {
                return event;
            }
        };

        final EventCollector collector = new BlockingCollector(100);

        OutboundConnection oc = new OutboundConnection(new Host("localhost",
                port), collector, transformer);

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

    @Test
    public void useTransformer_VerifyEventsGetTransformed() {

        // TODO: Create an event transformer that adds a json field to all
        // incoming events

        // TODO: Create an event transformer that drops every second event
        // by returning null
    }

    @After
    public void tearDown() {
        mockServer.interrupt();
        try {
            mockServer.join(500);
        } catch (Exception e) {
        }
    }
}
