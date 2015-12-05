package com.umermansoor.trafficdistributor.collectors;

import org.slf4j.Logger;

/**
 * A blocking collector which discards new events when attempting to store them
 * into this collector and it is full.
 * <p/>
 * This class is thread-safe.
 *
 * @author umermansoor
 */
public class DiscardOldestBlockingCollector extends BlockingCollector {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(DiscardNewestBlockingCollector.class);
    private final int maxSize;

    public DiscardOldestBlockingCollector(int capacity) {
        super(capacity);
        maxSize = capacity;
    }

    public synchronized boolean put(String event) {
        if (super.eventsQueue.size() == maxSize) {
            eventsQueue.remove();
        }

        eventsQueue.add(event);
        return true;
    }

}
