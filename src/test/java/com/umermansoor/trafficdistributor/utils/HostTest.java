package com.umermansoor.trafficdistributor.utils;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class HostTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

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
        assertNotEquals("failure - unequal instances are equals.", h1, new Object());

        assertEquals("failure - hashcodes are not equal", h1.hashCode(), h2.hashCode());
    }

    public void testInvalidCreation() throws NullPointerException, IllegalArgumentException {
        thrown.expect(NullPointerException.class);
        new Host(null, 1);

        thrown.expect(IllegalArgumentException.class);
        new Host("", 1);

        thrown.expect(IllegalArgumentException.class);
        String veryLongInvalidHostname = "Lorem ipsum dolor sit amet, nonummy ligula volutpat hac integer nonummy. Suspendisse ultricies, congue etiam tellus, erat libero, nulla eleifend, mauris pellentesque. Suspendisse integer praesent vel, integer gravida mauris, fringilla vehicula lacinia non2";
        new Host(veryLongInvalidHostname, 1);

        /** Invalid port numbers **/
        thrown.expect(IllegalArgumentException.class);
        new Host("localhost", -1);

        thrown.expect(IllegalArgumentException.class);
        new Host("localhost", 65536);
    }


}
