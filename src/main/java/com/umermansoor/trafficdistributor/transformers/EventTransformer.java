package com.umermansoor.trafficdistributor.transformers;

/**
 * This is used to filter or transform events either at the time they are
 * received, or before they are sent to a client.
 *
 * @author umermansoor
 */
public class EventTransformer {
    private static EventTransformer instance = null;

    private EventTransformer() {

    }

    public static EventTransformer getInstance() {
        if (instance == null) {
            instance = new EventTransformer();
        }

        return instance;
    }

    /**
     * This method is called when an event is received and before it is sent to
     * the collector. Place your business logic here to transform the event or
     * apply a filter. To drop events, simply returned null.
     *
     * @param event
     * @return processed event or null
     */
    public String processEvent(String event) {
        return event;
    }
}
