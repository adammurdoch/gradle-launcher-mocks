#include <string>
#include <iostream>
#include <fcntl.h>
#include <unistd.h>

using namespace std;

int main() {
    cout << "C++ client" << endl;
    try {
        const char* file = "../server/build/server.bin";
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

        uint16_t port = buffer[0] | buffer[1] << 8;
        cout << "server port: " << port << endl;
    } catch (exception* e) {
        cerr << e->what() << endl;
        return 1;
    }

    return 0;
}