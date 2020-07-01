//
// Created by vaismav@wincs.cs.bgu.ac.il on 1/6/19.
//

#ifndef UNTITLED_SOCKETDECODE_H
#define UNTITLED_SOCKETDECODE_H
#include <iostream>
#include <boost/asio.hpp>
#include "ConnectionHandler.h"

class SocketDecode {

public:

    SocketDecode(ConnectionHandler &handler, bool* terminated, bool* shouldTerminate);
    void run();

private:

    short bytesToShort(char *bytesArr);
    std::string dictionary(short op);
    void notificationDecoder(std::vector<std::string> vector);
    short shortDecoder(int size);
    std::string stringDecoder();
    void AckDecoder(std::vector<std::string> vector);

    ConnectionHandler &handler;
    bool* terminated;
    bool* shouldTerminate;
};

#endif