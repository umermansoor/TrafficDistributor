package com.umermansoor.trafficdistributor.config;

import com.umermansoor.trafficdistributor.collectors.DiscardOldestBlockingCollector;
import com.umermansoor.trafficdistributor.collectors.EventCollector;
import com.umermansoor.trafficdistributor.utils.Host;

import java.util.ArrayList;


/**
 * This class contains configuration parameters for this application.
 *
 * @author umer mansoor
 */
public class Configuration {


    /**
     * Number of seconds the socket can remain idle waiting to receive data
     * from the server. If this times out, the socket will be closed.
     */
    public static final int SOCKET_TIMEOUT_SECONDS = 18 * 1000;

    /**
     * Set this to `true` to force the app to keep retrying server connection.
     */
    public static final boolean CONNECTION_RETRY_FOREVER = true;

    /**
     * Seconds to wait before re-attempting connection.
     */
    public static final int CONNECTION_RETRY_DELAY_SECONDS = 5 * 1000;

    public static final ArrayList<Host> servers = new ArrayList<Host>(2);
    /**
     * Port for accepting incoming connections from clients.
     */
    public static final int LISTENING_PORT = 7002;
    public static final int MAX_CLIENT_CONNECTIONS = 100;
    public static final boolean TRACE_INCOMING_EVENTS = true;
    /**
     * Size of the collector
     */
    private static int COLLECTOR_CAPACITY = 20;
    /**
     * Specify the type of collector where events are stored before being
     * forwarded to clients.
     */
    public static final EventCollector EVENTS_COLLECTOR = new
            DiscardOldestBlockingCollector(COLLECTOR_CAPACITY);

    static {
        servers.add(new Host("localhost", 6001));
        servers.add(new Host("localhost", 6001));
    }


}
