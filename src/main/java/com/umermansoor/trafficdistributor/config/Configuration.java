package com.umermansoor.trafficdistributor.config;

import com.umermansoor.trafficdistributor.collectors.DiscardOldestBlockingCollector;
import com.umermansoor.trafficdistributor.collectors.EventCollector;
import com.umermansoor.trafficdistributor.transformers.EventTransformer;
import com.umermansoor.trafficdistributor.utils.Host;


/**
 * This class contains configuration parameters for this application.
 *
 * @author umer mansoor
 */
public class Configuration {
    /**
     * Specify one or more servers to connect to.
     */
    public final Host[] servers = new Host[]{
            // The host information below is for demo purposes. The supplied
            // demo server, `tools/dummy_server.py` runs on these ports if
            // started. Provide your own hosts here.
            new Host("localhost", 6001),
            new Host("localhost", 6001)
    };


    /**
     * Set this to `true` to force the app to keep retrying server connection.
     */
    public final boolean CONNECTION_RETRY_FOREVER = true;


    /**
     * Port for accepting incoming connections from clients.
     */
    public final int LISTENING_PORT = 7002;

    /**
     * Maximum number of client connections to allow.
     */
    public final int MAX_CLIENT_CONNECTIONS = 100;

    /**
     * Will log all incoming events if set to true.
     */
    public final boolean TRACE_INCOMING_EVENTS = true;
    /**
     * Specify the Event Transformer class. This class is given an opportunity
     * to transform, filter, modify or drop incoming events.
     */
    public final EventTransformer EVENTS_TRANSFORMER = new
            EventTransformer();
    /**
     * Size of the collector. This is the maximum number of events that could
     * be stored in the collector while waiting for clients to retreive them.
     */
    private int COLLECTOR_CAPACITY = 20;
    /**
     * Specify the type of collector where events are stored before being
     * forwarded to clients.
     */
    public final EventCollector EVENTS_COLLECTOR = new
            DiscardOldestBlockingCollector(COLLECTOR_CAPACITY);

}
