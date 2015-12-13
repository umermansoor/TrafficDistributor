# JSON TCP Distributor [![Build Status](https://travis-ci.org/umermansoor/TrafficDistributor.svg?branch=master)](https://travis-ci.org/umermansoor/TrafficDistributor) [![codecov.io](https://codecov.io/github/umermansoor/TrafficDistributor/coverage.svg?branch=master)](https://codecov.io/github/umermansoor/TrafficDistributor?branch=master)

Overview
========
TCP Distributor is a proxy/load balancer for spreading JSON events across backend servers.

![alt tag](docs/overall_idea.png)

Features:
 
1. The front-end can act as a server listening for connections or open connections itself as a client.
2. It works with servers that stream JSON events.
3. Throttle or limit the rate of incoming events.
4. Scrub, filter or transform incoming events.
5. Load balance decisions are taken for each event.
6. Distribute incoming events to back-end nodes.
 
JSON TCP Distributor works with JSON, but will also work with CSV or any String. The only requirement is that the
events must be separated by new lines.

Requirements
============

* Java SE 7.
* Apache Maven.
* Works on Linux, Windows, Mac OSX and (quite possibly) BSD.
* Python (Optional, to run the supplied dummy server).

Quick Start
===========

#### 1. Build the project. 

Go to the main project folder `TrafficDistributor/` and run the following command:

<code>
$ mvn clean package
</code>

#### 2. Start the dummy server

A dummy server is provided which sends random JSON strings to clients over TCP. To start the server:

<code>
$ python2.7 tools/dummy_server.py &
</code>

Note: Read the Settings section below to learn how to configure this app to connect to your servers.

#### 3. Run the app

<code>
$ java -jar target/TrafficDistributor-1.0-SNAPSHOT-jar-with-dependencies.jar
</code>

To stop the app once it is running, press `CTRL+C`.

Settings
========

Modify class `com.umermansoor.trafficdistributor.config.Configuration` to change settings.

Major Components
================

## 1. Front-end (Outbound or InboundConnections)

These modules handle communication with servers which produce JSON events. They take care of everything including
dealing with errors such as lost connections. All incoming events are first passed through 
**Transformers** before being stored in **Collectors**.

## 2. Transformers to Scrub Events

Transformer is called when an event is received and given an opportunity to apply a filter, threshold or
modify the event. For example, a simple transformer could check if the event is invalid and 
discard it without forwarding it to clients. 

## 3. Collectors to Limit Rate

These modules form the central repository where events are stored and later retrieved to be sent to clients.  This 
allows the app to keep event producers decoupled from event consumers. Various types of collectors are provided 
depending on the kind of behavior required when the collector is full:

* `BlockingCollector`: Waits for space to become available if the collector is full.
* `DiscardNewestBlockingCollector`: Rejects new events if the collector is full.
* `DiscardOldestBlockingCollector`: Discards oldest event if the collector is full.

## 5. Back-end (Inbound Connections)

These modules handle communication with the back-end clients and sends events to them. A TCP server is started
to allow clients to connect with the app.

FAQ
===

## Why not use HAProxy?

The front-end of HAProxy waits for incoming connections, whereas Traffic Distributor can also establish connections. In
Traffic Distributor load balancing decisions are taken for each incoming JSON event. 

## Why not use a Message Broker or Messaging Middleware?

The short answer is that you could and **you should** if your requirements allow for it. The reason I created this app
was because I often needed similar functionality when building a large mission critical platform that had
multiple 3rd party servers sending event streams (thousands of events per second). 
Those events were to be filtered, transformed and distributed before being sent to a cluster of back-end nodes 
for further processing.

## Where to specify the servers this app should connect to in order to receive data?

You can specify this and any other configuration information in the configuration class in 
`src/main/java/com/umermansoor/trafficdistributor/config/Configuration.java`

## Is this project suitable for running in the background as a service?

Yes. By default, it will automatically re-establish with the servers if the connection is lost. All collectors are
designed to work even when they are full. By default, the app uses `DiscardOldestBlockingCollector` which will 
discard oldest events if the collector is full (this might happen if no clients are connected to consume events).

 
Known Issues
============

* Does not detect and deal with TCP Half-Open Connections. For example, if the client (events receiver) process 
crashes and the socket is abruptly closed, the app may not detect the failure and continue to send events which
may result in a loss of data. Implement a PING-PONG or another keep-alive mechanism.




