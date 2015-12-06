package com.umermansoor.trafficdistributor.collectors;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class BlockingCollectorTest {

    @Test
    public void testSize() {
        BlockingCollector blc = new BlockingCollector(10);
        try {
            blc.put("1");
            assertEquals(1, blc.size());
            blc.put("2");
            assertEquals(2, blc.size());
            blc.put("3");
            assertEquals(3, blc.size());
        } catch (Exception e) {
            fail("exception thrown");
        }
    }

    @Test
    public void testDropAll() {
        BlockingCollector blc = new BlockingCollector(10);
        try {
            blc.put("1");
            blc.put("2");
            blc.put("3");
            assertEquals(3, blc.size());
            blc.dropAll();
            assertEquals(0, blc.size());
        } catch (Exception e) {
            fail("exception thrown");
        }
    }

    @Test
    public void testStoreAndRetrieve() {
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
