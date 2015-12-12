package com.umermansoor.trafficdistributor.collectors;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class BlockingCollectorTest {

    @Test
    public void size() {
        BlockingCollector blc = new BlockingCollector(10);
        try {
            for (int i = 1; i <= 10; i++) {
                blc.put(String.valueOf(i));
                assertEquals(i, blc.size());
            }

        } catch (Exception e) {
            fail("exception thrown");
        }
    }

    @Test
    public void dropAll() {
        int capacity = 10;
        BlockingCollector blc = new BlockingCollector(capacity);
        try {
            for (int i = 1; i <= capacity; i++) {
                blc.put(String.valueOf(i));
                assertEquals(i, blc.size());
            }
            assertEquals(capacity, blc.size());

            // Now drop all elements and verify that there are 0 elements remaining.
            blc.dropAll();
            assertEquals(0, blc.size());
        } catch (Exception e) {
            fail("exception thrown");
        }
    }

    @Test
    public void get_ElementsRetrievedInOrder() {
        BlockingCollector blc = new BlockingCollector(10);
        try {
            String[] events = new String[]{"1", "2", "3", "4", "5"};

            for (String s : events) {
                blc.put(s);
            }

            for (String s : events) {
                String r = blc.get();
                assertEquals(s, r);
            }
        } catch (Exception e) {
            fail("exception thrown");
        }
    }
}
