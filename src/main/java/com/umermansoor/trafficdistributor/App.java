package com.umermansoor.trafficdistributor;

import com.umermansoor.trafficdistributor.net.OutboundConnectionManager;
import com.umermansoor.trafficdistributor.util.Host;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class App {

    public static void main(String[] args) {
        ArrayList<Host> hosts = new ArrayList<Host>(1);
        hosts.add(new Host("localhost", 6001));
        LinkedBlockingQueue<String> centralQueue = new LinkedBlockingQueue<String>();

        OutboundConnectionManager connectionManager = new OutboundConnectionManager(
                hosts, centralQueue);
        new Thread(connectionManager).start();

    }
}
