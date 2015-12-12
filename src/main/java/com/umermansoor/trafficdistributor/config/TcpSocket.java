package com.umermansoor.trafficdistributor.config;


/**
 * Connection specific settings.
 */
public class TcpSocket {
    /**
     * Seconds to wait before re-attempting server connection.
     */
    public static final int CONNECTION_RETRY_DELAY_SECONDS = 1 * 1000;

    /**
     * Number of seconds the socket can remain idle waiting to receive data
     * from the server. If this times out, the socket will be closed.
     */
    public static final int SOCKET_TIMEOUT_SECONDS = 18 * 1000;
}
