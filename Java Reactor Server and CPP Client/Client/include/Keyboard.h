
#ifndef UNTITLED_CONVERTOR_H
#define UNTITLED_CONVERTOR_H

#include <iostream>
#include <vector>
#include "ConnectionHandler.h"


class Keyboard {

    public:

    Keyboard(ConnectionHandler &handler, bool* terminated, bool* shouldTerminate);
    void run();
    void encodeProcess(const std::string &basic_string);

    private:

    void followEncoder(std::string basic_string);

    void encodeOfStrings(std::string frame);

    void contentEncoder(std::string basic_string);

    void encodePM(std::string basic_string);

    void bytesSender();

    std::string cutOpCodeFromFrame(const std::string &frame);

    int findOpCode(const std::string &frame);

    void shortToBytes(short num, char* bytesArr);

    // Return vector of all the strings in the frame.
    std::vector<std::string> splitToStrings(const std::string &basic_string);

    ConnectionHandler &handler;
    bool* terminated;
    bool* shouldTerminate;
    std::vector<char> output;
    int opCode = 0;

}; //class Keyboard


#endif