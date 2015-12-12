package com.umermansoor.trafficdistributor.net;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * A mock TCP server. Accepts a single client only.
 * Sends a finite stream of JSON event.
 *
 * @author umermansoor
 */
public class MockServer extends Thread {
    protected static final String[] dataToSend = new String[]{
            "{\"id\": 1}",
            "{\"id\": 2}",
            "{\"id\": 3}",
            "{\"id\": 4}",
            "{\"id\": 5}",
            "{\"id\": 6}",
            "{\"id\": 7}",
    };

    private final int port;
    protected volatile boolean clientConnected;
    protected volatile boolean bindSuccessfull;
    private ServerSocket server;

    public MockServer(int p) {
        port = p;
    }

    @Override
    public void run() {
        startServer();
    }

    @Override
    public void interrupt() {
        if (server != null) {
            try {
                server.close();
            } catch (java.io.IOException ignored) {
                System.err.println("error closing mock server.");
            }
        }

        super.interrupt();
    }

    /**
     * Starts a TCP server to listen for clients. Exits if the server couldn't
     * be started.
     */
    private void startServer() {
        try {
            server = new ServerSocket(port);
        } catch (Exception e) {
            System.err.println("failed to bind mock server to port " + port + ". " + e.toString());
            return;
        }

        bindSuccessfull = true;
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Socket client = server.accept();
                clientConnected = true;

                BufferedWriter out = new BufferedWriter(new
                        OutputStreamWriter(client.getOutputStream()));

                // Client connected, send the data and close the connection.
                for (String data : dataToSend) {
                    out.write(data);
                    out.newLine();
                    out.flush();
                    //System.out.println("+" + data);
                }
                client.close();
            } catch (java.io.IOException ioe) {
                System.err.println("error in mock server. ok if shut down was requested.");
                break;
            }
        }
    }

}
