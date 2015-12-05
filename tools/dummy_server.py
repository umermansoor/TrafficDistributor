# A very, very basic TCP server which sends random streams of JSON to clients
# over TCP.
# author: umer

import json
import select
import socket
from datetime import datetime
from random import randint
from threading import Thread
from time import sleep

PORT = 6001
MAX_CLIENTS = 5


class DataProducerThread(Thread):
    def __init__(self, q):
        super(DataProducerThread, self).__init__()
        self.running = True
        self.q = q

    def stop(self):
        self.running = False

    def run(self):
        while self.running:
            self.q.put(randint(1, 999), timeout=1)


class ClientHandler(Thread):
    def __init__(self, c):
        super(ClientHandler, self).__init__()
        self.running = True
        self.client = c

    def stop(self):
        self.running = False

    def run(self):
        while self.running:
            try:
                self.client.send(json.dumps(
                    {'_id': randint(1, 999),
                     'time': datetime.now().microsecond}) + '\n')
                sleep(0.1)  # Throttle
            except:
                print "- error sending data. closing client connection."
                self.client.close()
                break


if __name__ == "__main__":
    s = socket.socket()
    s.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    s.bind(('127.0.0.1', PORT))
    s.listen(MAX_CLIENTS)

    print "server running on port", PORT

    while True:
        try:
            client, addr = s.accept()
            print "+ new client connected"
            ready = select.select([client, ], [], [], 2)
            c = ClientHandler(client)
            c.daemon = True
            c.start()
        except KeyboardInterrupt:
            print "stop signal received."
            break
        except socket.error, msg:
            print "socket error! %s" % msg
            break

    s.close()

    print "bye."
