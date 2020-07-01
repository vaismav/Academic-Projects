//
// Created by vaismav@wincs.cs.bgu.ac.il on 1/6/19.
//

#include "../include/SocketDecode.h"

SocketDecode::SocketDecode(ConnectionHandler &handler,bool* terminated, bool* shouldTerminate): handler(handler), terminated(terminated), shouldTerminate(shouldTerminate){}

void SocketDecode::run(){
    while(!*terminated){
        std::vector<std::string> args;
        short op = shortDecoder(2);
        args.push_back(dictionary(op));
        if (op == 9){ notificationDecoder(args);}
        else{
            short msgOp = shortDecoder(2);
            args.push_back(std::to_string(msgOp));
            if (op == 10){
                AckDecoder(args);
            }
            else if (op == 11) {
                if (args[1] == "3"){
                    *shouldTerminate = false;
                } //logout failed
               std::cout<<args[0] + " " + args[1]<< std::endl;
            }

        }
    }
}

void SocketDecode::AckDecoder(std::vector<std::string> args) {
    if (args[1] == "1"|| args[1] == "2" || args[1] == "5" || args[1] == "6"){
        std::cout<< args[0] + " " + args[1]<< std::endl;
    }
    else if(args[1] == "3"){
        std::cout<< args[0] + " " + args[1]<< std::endl;
        *terminated = true; //logout success
    }
    else if(args[1] == "4" || args[1] == "7"){
        int numOfUsers = shortDecoder(2);
        args.push_back(std::to_string(numOfUsers));
        std::string userNameList;
        for (size_t i = 0; i < numOfUsers; i++) {
            std::string userName = stringDecoder();
            userNameList += userName + " ";
        }
        std::cout<<args[0] + " " + args[1] + " " + args[2] + " " + userNameList<< std::endl;
    }
    else if(args[1] == "8"){
        short NumPosts = shortDecoder(2);
        short NumFollowers = shortDecoder(2);
        short NumFollowing = shortDecoder(2);
        args.push_back(std::to_string(NumPosts));
        args.push_back(std::to_string(NumFollowers));
        args.push_back(std::to_string(NumFollowing));
        std::cout<< args[0] + " " + args[1] + " " + args[2] + " " + args[3] + " " + args[4]<< std::endl;
    }
}

void SocketDecode::notificationDecoder(std::vector<std::string> args) {
    char arg[1];
    handler.ConnectionHandler::getBytes(arg,1);
    if (arg[0] == '\0') {
        args.push_back("PM");
    } else {
        args.push_back("PUBLIC");
    }
    std::string user = stringDecoder();
    args.push_back(user);
    std::string content = stringDecoder();
    args.push_back(content);
    std::cout << args[0] + " " + args[1] + " " + args[2] + " " + args[3] << std::endl;
}

    short SocketDecode::shortDecoder (int bytes){
        char arr[bytes];
        handler.ConnectionHandler::getBytes(arr,bytes);
        short msg = bytesToShort(arr);
        return msg;
}


std::string SocketDecode::dictionary(short op) {
    if (op == 9){return "NOTIFICATION";}
    else if (op == 10){return "ACK";}
    else if (op == 11){return "ERROR";}
}

short SocketDecode::bytesToShort(char* bytesArr) {
    short result = (short)((bytesArr[0] & 0xff) << 8);
    result += (short)(bytesArr[1] & 0xff);
    return result;
}

std::string SocketDecode::stringDecoder() {
    std::string arg;
    handler.getLine(arg);
    return arg.substr(0,arg.length()-1);
}
