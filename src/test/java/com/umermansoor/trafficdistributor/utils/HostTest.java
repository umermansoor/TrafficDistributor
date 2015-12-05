package com.umermansoor.trafficdistributor.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class HostTest {

    @Test
    public void testHost() {
        String hostname = "0.0.0.1";
        int port = 6543;

        Host h = new Host(hostname, port);
        assertEquals("failure - wrong hostname returned.", hostname, h.getHostname());
        assertEquals("failure - wrong port returned.", port, h.getPort());
    }

    @Test
    public void testHostEquals() {
        Host h1 = new Host("0.0.0.1", 6543);
        Host h2 = new Host("0.0.0.1", 6543);
        Host h3 = new Host("0.0.0.1", 6544);

        assertEquals("failure - equal instances are not equals.", h1, h2);
        assertNotEquals("failure - unequal instances are equals.", h1, h3);




    }

}
