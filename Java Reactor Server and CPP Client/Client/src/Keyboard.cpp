//
// Created by vaismav@wincs.cs.bgu.ac.il on 1/5/19.
//
#include <iostream>
#include <boost/asio.hpp>
#include "../include/Keyboard.h"
#include "../include/ConnectionHandler.h"
#include <iostream>
#include <string>
#include <vector>


Keyboard::Keyboard(ConnectionHandler &handler,bool* terminated, bool* shouldTerminate): handler(handler), terminated(terminated), shouldTerminate(shouldTerminate),opCode(0),output(){}

void Keyboard::run() {
    while (!*terminated) {
        while (!*shouldTerminate) {
            char buf[1024];
            std::cin.getline(buf, 1024);
            std::string line(buf);
            encodeProcess(line);
        }
    }
}

void Keyboard::encodeProcess(const std::string &frame) {
    opCode = findOpCode(frame);
    char bytes[2];
    shortToBytes((short)opCode, bytes);
    output.push_back(bytes[0]);
    output.push_back(bytes[1]);
    if (opCode == 3){
        *shouldTerminate = true;
    }//wait for ACK/ERROR for logout msg
    std::string editedFrame = cutOpCodeFromFrame(frame);
    if (opCode == 1|| opCode == 2 || opCode == 8) {
        encodeOfStrings(editedFrame);
    }
    else if (opCode == 4){
        followEncoder(editedFrame);
    }
    else if (opCode == 5){
        contentEncoder(editedFrame);
    }
    else if (opCode == 6){
        encodePM(editedFrame);
    }
    if(opCode>0)
        bytesSender();

    output.clear();
}

void Keyboard::contentEncoder(std::string content) {
    for (size_t i = 0; i < content.length(); i++) {
        output.push_back(content.at(i));
    }
    output.push_back('\0');
//    std::cout << content<<std::endl;
}
std::string Keyboard::cutOpCodeFromFrame(const std::string &frame) {
    if (opCode == 1) {
        return frame.substr(9);
    } else if (opCode == 2) {
        return frame.substr(6);
    } else if (opCode == 4) {
        return frame.substr(7);
    } else if (opCode == 5 || opCode == 8) {
        return frame.substr(5);
    } else if (opCode == 6) {
        return frame.substr(3);
    }
    std::string empty; //else of opcode is neither 7\3
    return empty;
}

    void Keyboard::encodeOfStrings(std::string editedFrame) {
    std::vector<std::string> args = splitToStrings(editedFrame);
    for (size_t i = 0; i < args.size(); i++) {
        for (size_t j = 0; j < args[i].length(); j++) {
            if (args[i] != " ") {
                output.push_back(args[i].at(j));
            } else { output.push_back('\0'); }
        }
        output.push_back('\0');
    }
}

    void Keyboard::followEncoder(std::string editedFrame) {
        std::vector<std::string> args = splitToStrings(editedFrame);
        output.push_back(args[0].at(0) - '0');
        char bytesNum[2];
        shortToBytes(short(std::stoi(args[1])), bytesNum);
        output.push_back(bytesNum[0]);
        output.push_back(bytesNum[1]);
        for (size_t i =2; i < args.size(); i++)
        encodeOfStrings(args[i]);

    }

    void Keyboard::encodePM(std::string editedFrame) {
        unsigned long startContent = editedFrame.find(' ');
        for (size_t i = 0; i < startContent; i++) {
            output.push_back(editedFrame.at(i));
        }
        output.push_back('\0');
        contentEncoder(editedFrame.substr(startContent + 1));
    }


    int Keyboard::findOpCode(const std::string &frame) {
        if (frame.substr(0, 2) == "US") { return 7; }
        if (frame.substr(0, 2) == "LO" && frame.at(5) == 'T') { return 3; }
        unsigned long flag;
        flag = frame.find(' ');
        std::string operation = frame.substr(0, flag);
        if (operation == "REGISTER") { return 1; }
        else if (operation == "LOGIN") { return 2; }
        else if (operation == "FOLLOW") { return 4; }
        else if (operation == "POST") { return 5; }
        else if (operation == "PM") { return 6; }
        else if (operation == "STAT") { return 8; }
        else return 0;
    }

    std::vector<std::string> Keyboard::splitToStrings(const std::string &frame) {
        std::vector<std::string> args;
        std::stringstream sts(frame);
        std::string string;
        while (std::getline(sts, string, ' ')) {
            args.push_back(string);
        }
        return args;
    }

    void Keyboard::shortToBytes(short num, char *bytesArr) {
        bytesArr[0] = char(((num >> 8) & 0xFF));
        bytesArr[1] = char((num & 0xFF));
    }

    void Keyboard::bytesSender() {
        unsigned long outputSize = output.size();
        char bytes[outputSize];
        for (size_t i = 0; i < outputSize; i++) {
            bytes[i] = output[i];
        }
        output.clear();
        if (!handler.sendBytes(bytes, (int) outputSize)) {
            std::cout << "Disconnected" << std::endl;
        }
    }







