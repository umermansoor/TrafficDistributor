package com.umermansoor.trafficdistributor.collectors;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class DiscardOldestBlockingCollectorTest {
    @Test
    public void testBehavior() {
        int capacity = 4;
        DiscardOldestBlockingCollector oblc = new DiscardOldestBlockingCollector(capacity);
        assertEquals(0, oblc.size());
        try {
            String[] events = new String[]{"1", "2", "3", "4", "5", "6", "7", "8"};

            for (String s : events) {
                oblc.put(s);
            }
            assertEquals(capacity, oblc.size());

            for (int i = capacity; i < events.length; i++) {
                assertEquals(events[i], oblc.get());
            }

        } catch (Exception e) {
            fail("exception thrown");
        }
    }

}
