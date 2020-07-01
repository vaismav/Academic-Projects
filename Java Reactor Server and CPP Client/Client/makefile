# define some Makefile variables for the compiler and compiler flags
# to use Makefile variables later in the Makefile: $()
CC = g++
CFLAGS:=-c -Wall -Weffc++ -g -std=c++11 -Iinclude
LDFLAGS:=-lboost_system -pthread

# All Targets
all: BGSclient

# Tool invocations
# Executable "hello" depends on the files hello.o and run.o.
BGSclient: bin/ConnectionHandler.o bin/BGSClient.o bin/Keyboard.o bin/SocketDecode.o
	@echo 'Building target: BGSClient'
	@echo 'Invoking: C++ Linker'
	$(CC) -o bin/BGSclient  bin/ConnectionHandler.o bin/BGSClient.o bin/Keyboard.o bin/SocketDecode.o  $(LDFLAGS)
	@echo 'Finished building target: BGSClient'
	@echo ' '

bin/ConnectionHandler.o: src/ConnectionHandler.cpp
	$(CC) $(CFLAGS) -c -Iinclude -o bin/ConnectionHandler.o src/ConnectionHandler.cpp

bin/SocketDecode.o: src/SocketDecode.cpp
	$(CC) $(CFLAGS) -c -Iinclude -o bin/SocketDecode.o src/SocketDecode.cpp

bin/Keyboard.o: src/Keyboard.cpp
	$(CC) $(CFLAGS) -c -Iinclude -o bin/Keyboard.o src/Keyboard.cpp


bin/BGSClient.o: src/BGSClient.cpp
	$(CC) $(CFLAGS) -c -Iinclude -o bin/BGSClient.o src/BGSClient.cpp
#Clean the build directory
clean: 
	rm -f bin/*
