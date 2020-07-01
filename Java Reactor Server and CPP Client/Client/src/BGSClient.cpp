#include <stdlib.h>
#include <iostream>
#include "../include/ConnectionHandler.h"
#include "../include/SocketDecode.h"
#include <mutex>
#include <thread>
#include "../include/Keyboard.h"

int main (int argc, char *argv[]) {
    if (argc < 3) {
        std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
        return -1;
    }
    std::string host = argv[1];
    short port = atoi(argv[2]);

    ConnectionHandler handler(host, port);
    if (!handler.connect()) {
        std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
        return 1;
    }
    bool* shoutTerminate = new bool(false);
    bool* terminated = new bool(false);
    SocketDecode socketDecode(handler,terminated,shoutTerminate);
    Keyboard keyboard(handler,terminated,shoutTerminate);
    std::thread writer(&Keyboard::run, &keyboard);
    std::thread receiveAndPrint(&SocketDecode::run, &socketDecode);
    writer.join();
    receiveAndPrint.join();

    delete shoutTerminate;
    delete terminated;
    return 0;
}