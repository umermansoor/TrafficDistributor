package com.umermansoor.trafficdistributor.transformers;

/**
 * This class is given an opportunity to transform or filter an event when it
 * is received but before it is stored in the collector.
 *
 * TODO: Modify this to add your own business logic or behavior.
 *
 * @author umermansoor
 */
public class EventTransformer {

    public EventTransformer() {

    }


    /**
     * This method is called when an event is received and before it is stored
     * in the collector. Place your business logic here to transform the event,
     * apply a filter or to keep metrics.
     *
     * To drop the given event, return null.
     *
     * @param event
     * @return processed event or null
     */
    public String processEvent(String event) {
        return event;
    }
}
