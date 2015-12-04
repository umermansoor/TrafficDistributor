# Traffic Director

Overview
========
Traffic Director splits incoming events from multiple **servers** to multiple **clients**. 

![alt tag](docs/overall_idea.png)

It establishes TCP connections with servers. The incoming events (new line separated strings) are placed in a 
central queue. When a client connects, the events from the queue are sent to it. Traffic Director supports multiple 
clients and an event distribution policy is applied in that case.

Requirements
============

* Java SE 7
* Apache Maven 
* Works on Linux, Windows, Mac OSX and (quite possibly) BSD.

Install
=======

Maven... #TODO

Why not use a Message Broker or Messaging Middleware?
=====================================================

The short answer is that you could and you should if your requirements allow for it (i.e. you have full control over 
the events producers and could modify their behavior to send events to a broker).

I built a similar project to retrieve tens of thousands of events from several 3rd party sniffers and 
distribute them to a cluster of clients. When an event is received, a filter is applied and the event is dropped if
some condition is met (e.g. invalid event, threshold reached, etc.)







