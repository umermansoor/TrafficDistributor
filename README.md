# JSON TCP Distributor

Overview
========
JSON TCP Distributor is used for efficiently distributing incoming JSON events over TCP across multiple back-end 
nodes. 

![alt tag](docs/overall_idea.png)

Use this app if you need to:
 
1. Connect to one or more servers which send a continuous stream of JSON (or CSV or Strings), and,
2. Optionally filter or transform incoming events (e.g. apply throttle, drop invalid, tag etc.), and,
3. Distribute incoming events to two or more back-end nodes.
 
JSON TCP Distributor works with JSON, but will also work with CSV or any String. The only requirement is that the
events must be separated by new lines.

Requirements
============

* Java SE 7
* Apache Maven 
* Works on Linux, Windows, Mac OSX and (quite possibly) BSD.

Quick Start
===========

To build the JAR file, go to the main project folder and run the following command:

<code>
$ mvn clean package
</code>

To execute the JAR file:

<code>
$ java -jar target/TrafficDistributor-1.0-SNAPSHOT-jar-with-dependencies.jar
</code>

To kill the server once it is running, press `CTRL+C`.

Settings
=============

Modify class `com.umermansoor.trafficdistributor.config.Configuration` to change settings.

Major Components
================

## 1. Outbound Connections

These modules handle communication with servers which produce JSON events. They take care of everything including
dealing with errors such as lost connections. All incoming events are first passed through 
**Transformers** before being stored in **Collectors**.

## 2. Transformers

Transformer is called when an event is received and given an opportunity to apply a filter, threshold or
modify the event. For example, a simple transformer could check if the event is invalid and 
discard it without forwarding it to clients. 

## 3. Collectors

These modules form the central repository where events are stored and later retrieved to be sent to clients. This 
allows the app to keep event producers decoupled from event consumers. Various types of collectors are provided 
depending on the kind of behavior required when the collector is full:

* `BlockingCollector`: Waits for space to become available if the collector is full.
* `DiscardNewestBlockingCollector`: Rejects new events if the collector is full.
* `DiscardOldestBlockingCollector`: Discards oldest event if the collector is full.

## 4. Inbound Connections

These modules handle communication with the back-end clients and sends events to them. A TCP server is started
to allow clients to connect with the app.

FAQ
===

## Why not use a Message Broker or Messaging Middleware?

The short answer is that you could and **you should** if your requirements allow for it. The reason I created this app
was because I often needed similar functionality when building a large mission critical platform that had
multiple 3rd party servers sending event streams (thousands of events per second). 
Those events were to be filtered, transformed and distributed before being sent to a cluster of back-end nodes 
for further processing.
 





