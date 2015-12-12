package com.umermansoor.trafficdistributor.collectors;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class DiscardNewestBlockingCollectorTest {

    @Test
    public void put_DiscardsNewElementsWhenFull() {
        int capacity = 4;
        DiscardNewestBlockingCollector discardNewElements = new DiscardNewestBlockingCollector(capacity);

        try {
            String[] events = new String[]{"1", "2", "3", "4", "5", "6", "7", "8"};

            // Try to put more elements into the collector than its capacity.
            for (String s : events) {
                discardNewElements.put(s);
            }

            assertEquals(capacity, discardNewElements.size());

            for (int i = 0; i < capacity - 1; i++) {
                assertEquals(events[i], discardNewElements.get());
            }

        } catch (Exception e) {
            fail("exception thrown");
        }
    }

}
