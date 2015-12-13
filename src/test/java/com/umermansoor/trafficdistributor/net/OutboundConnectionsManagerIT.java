package com.umermansoor.trafficdistributor.net;

import com.umermansoor.trafficdistributor.collectors.BlockingCollector;
import com.umermansoor.trafficdistributor.net.backend.OutboundConnectionsManager;
import com.umermansoor.trafficdistributor.transformers.EventTransformer;
import com.umermansoor.trafficdistributor.utils.Host;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Integrations Tests for {@link OutboundConnectionsManager}
 * It uses {@link MockServer} to start a mock TCP server.
 *
 * @author umermansoor
 */
public class OutboundConnectionsManagerIT {

    private final int portServer1 = 3334;
    private final int portServer2 = 3335;

    private MockServer mockServer1;
    private MockServer mockServer2;

    @Before
    public void setUp() {
        // Start servers. We pass `true` so the servers disconnect clients
        // immediately after they connect
        mockServer1 = new MockServer(portServer1, true);
        mockServer2 = new MockServer(portServer2, true);

        mockServer1.start();
        mockServer2.start();
    }

    @Test
    public void connectToMockServers_VerifyClientConnectivity() {
        Host[] hosts = new Host[]{
                new Host("localhost", portServer1),
                new Host("localhost", portServer2),
        };

        OutboundConnectionsManager ocm = new OutboundConnectionsManager(hosts,
                new BlockingCollector(100), new EventTransformer() {
            @Override
            // Return the same event back without modifications.
            public String processEvent(String event) {
                return event;
            }
        }, false);

        Thread t = new Thread(ocm);
        t.start();


        try {
            t.join(1500);
        } catch (InterruptedException ie) {
        }

        assertEquals(1, mockServer1.numClients.get());
        assertEquals(1, mockServer2.numClients.get());
    }

    @After
    public void tearDown() {
        mockServer1.interrupt();
        mockServer2.interrupt();
        try {
            mockServer1.join(500);
            mockServer2.join(500);
        } catch (Exception ignored) {
        }
    }
}