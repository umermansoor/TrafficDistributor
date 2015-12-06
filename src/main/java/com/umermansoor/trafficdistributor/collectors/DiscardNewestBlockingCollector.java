package com.umermansoor.trafficdistributor.collectors;

import org.slf4j.Logger;

import java.util.concurrent.TimeUnit;

/**
 * A blocking collector which rejects new events when attempting to store them
 * into this collector and it is full.
 *
 * This class is thread-safe.
 *
 * @author umermansoor
 */
public class DiscardNewestBlockingCollector extends BlockingCollector {

    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(DiscardNewestBlockingCollector.class);

    public DiscardNewestBlockingCollector(int capacity) {
        super(capacity);
    }

    /**
     * Stores the given event to this collector. If there is no more room, it
     * rejects the given event and returns false.
     * <p/>
     * Won't accept null event.
     *
     * @param event
     * @return true if the event is stored in the collector. false otherwise.
     * @throws InterruptedException
     */
    @Override
    public boolean put(String event) throws InterruptedException {
        // By default, we wait 1 second before giving up.
        if (!super.eventsQueue.offer(event, 1, TimeUnit.SECONDS)) {
            logger.error("queue full. no or slow clients connected? dropped event {}.", event);
            return false;
        }
        return true;
    }

}
