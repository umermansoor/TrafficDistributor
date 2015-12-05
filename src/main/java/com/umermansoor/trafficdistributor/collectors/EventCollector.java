package com.umermansoor.trafficdistributor.collectors;

/**
 * Event collector is used to store incoming events from servers before
 * forwarding them to clients. This interface represents the contract
 * that all event collectors must obey.
 *
 * @author umermansoor
 */
public interface EventCollector {
    String get() throws InterruptedException;

    boolean put(String event) throws InterruptedException;

    int size();

    void dropAll();

}
