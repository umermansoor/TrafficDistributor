package com.umermansoor.trafficdistributor.handlers;

/**
 * This is used to filter or transform events either at the time they are
 * received, or before they are sent to a client.
 */
public interface EventHandler {
    /**
     *
     * @param event
     * @return processed event or null
     */
    public String processEvent(String event);
}
