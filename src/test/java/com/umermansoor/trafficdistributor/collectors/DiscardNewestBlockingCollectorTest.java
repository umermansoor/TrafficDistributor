package com.umermansoor.trafficdistributor.collectors;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class DiscardNewestBlockingCollectorTest {
    @Test
    public void testBehavior() {
        int capacity = 4;
        DiscardNewestBlockingCollector nblc = new DiscardNewestBlockingCollector(capacity);
        assertEquals(0, nblc.size());
        try {
            String[] events = new String[]{"1", "2", "3", "4", "5", "6", "7", "8"};

            for (String s : events) {
                nblc.put(s);
            }
            assertEquals(capacity, nblc.size());

            for (int i = 0; i < capacity - 1; i++) {
                assertEquals(events[i], nblc.get());
            }

        } catch (Exception e) {
            fail("exception thrown");
        }
    }

}
