package com.umermansoor.trafficdistributor.collectors;

import org.slf4j.Logger;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * A blocking collector used for storing events.
 *
 * @author umermansoor
 */
public class BlockingCollector implements EventCollector {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(BlockingCollector.class);
    protected final LinkedBlockingQueue<String> eventsQueue;

    public BlockingCollector(int capacity) {
        eventsQueue = new LinkedBlockingQueue<String>(capacity);
    }

    public String get() throws InterruptedException {
        return eventsQueue.take();
    }

    public boolean put(String event) throws InterruptedException {
        eventsQueue.put(event);
        return true;
    }

    public int size() {
        return eventsQueue.size();
    }

    public void dropAll() {
        eventsQueue.clear();
    }
}
