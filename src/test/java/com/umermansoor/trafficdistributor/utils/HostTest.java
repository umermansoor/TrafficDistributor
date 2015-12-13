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
    public void equalsContract_SameHosts() {
        Host h1 = new Host("0.0.0.1", 6543);
        Host h2 = new Host("0.0.0.1", 6543);

        assertEquals(h1, h2);
        assertEquals(h1.hashCode(), h2.hashCode());
    }

    @Test
    public void equalsContract_DifferentHosts() {
        Host h1 = new Host("0.0.0.1", 6543);
        Host h2 = new Host("0.0.0.1", 9999);
        Host h3 = new Host("0.0.0.2", 6543);
        Object o = new Object();

        assertNotEquals(h1, h2);
        assertNotEquals(h1, h3);
        assertNotEquals(h1, o);

        assertNotEquals(h1.hashCode(), h2.hashCode());
        assertNotEquals(h1.hashCode(), h3.hashCode());
        assertNotEquals(h1.hashCode(), o.hashCode());

    }

    @Test
    public void constructor_NullHostname() {
        thrown.expect(NullPointerException.class);
        new Host(null, 1);
    }

    @Test
    public void constructor_EmptyHostnameString() {
        thrown.expect(IllegalArgumentException.class);
        new Host("", 1);
    }

    @Test
    public void constructor_VeryLongHostname() {
        thrown.expect(IllegalArgumentException.class);
        String veryLongInvalidHostname = "Lorem ipsum dolor sit amet, nonummy ligula volutpat hac integer nonummy. Suspendisse ultricies, congue etiam tellus, erat libero, nulla eleifend, mauris pellentesque. Suspendisse integer praesent vel, integer gravida mauris, fringilla vehicula lacinia non2";
        new Host(veryLongInvalidHostname, 1);
    }

    @Test
    public void constructor_NegativePortNumber() {
        thrown.expect(IllegalArgumentException.class);
        new Host("localhost", -1);
    }

    @Test
    public void constructor_InvalidPortNumber() {
        thrown.expect(IllegalArgumentException.class);
        new Host("localhost", 65536);
    }
}
