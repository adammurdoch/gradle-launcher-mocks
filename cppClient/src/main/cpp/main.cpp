#include <string>
#include <iostream>
#include <fcntl.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>

using namespace std;

uint16_t read_server_port() {
    const char* file = "build/server.bin";
    int fd = open(file, 0);
    if (fd < 0) {
        string message;
        message.append("Could not open file ");
        message.append(file);
        throw new runtime_error(message);
    }

    char buffer[2];
    int nread = read(fd, buffer, 2);
    if (nread != 2) {
        string message;
        message.append("Could not read server port from ");
        message.append(file);
        throw new runtime_error(message);
    }
    close(fd);

    return ((uint16_t) buffer[0] & 0xFF) | ((uint16_t) buffer[1] & 0xFF) << 8;
}

void write_request_message(int socket) {
    string message = "C++            ";
    uint8_t len = message.size();
    write(socket, &len, 1);
    write(socket, message.c_str(), len);
}

void read_response(int socket) {
    uint8_t len;
    read(socket, &len, 1);
    char buffer[len+1];
    read(socket, buffer, len);
    buffer[len] = 0;
    cout << "* Received: " << buffer << endl;
}

int connect_to_server(uint16_t port) {
    int fd = socket(PF_INET, SOCK_STREAM, 0);
    if (fd < 0) {
        throw new runtime_error("Could not open socket");
    }

    struct sockaddr_in server_addr;
    server_addr.sin_addr.s_addr = INADDR_ANY;
    server_addr.sin_family = AF_INET;
    server_addr.sin_port = htons(port);
    if (connect(fd, (const struct sockaddr *)&server_addr, sizeof(server_addr)) != 0) {
        close(fd);
        throw new runtime_error("Could not connect to server");
    }
    return fd;
}

void handle_request(uint16_t port) {
    int socket = connect_to_server(port);
    write_request_message(socket);
    read_response(socket);
    close(socket);
}

int main() {
    cout << "* C++ client" << endl;
    try {
        uint16_t port = read_server_port();
        cout << "* Server port: " << port << endl;
        handle_request(port);
    } catch (exception* e) {
        cerr << e->what() << endl;
        return 1;
    }
    return 0;
}
