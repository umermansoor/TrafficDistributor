package com.umermansoor.trafficdistributor;


public class Configuration {
    public static final int SOCKET_TIMEOUT = 18 * 1000;

    /**
     * Set this to `true` to keep attempting to connect to the server forever.
     * If the connection is lost, it will be retried forever.
     */
    public static final boolean CONNECTION_RETRY_FOREVER = true;
    /**
     * Seconds to wait before re-attemtping connection
     */
    public static final int CONNECTION_RETRY_DELAY = 5 * 1000;


}
