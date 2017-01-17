#include <stdlib.h>
#include <connectionHandler.h>
#include <ServerTask.h>
#include <boost/thread.hpp>

int main (int argc, char *argv[]) {
    if (argc < 3) {
        std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
        return -1;
    }
    std::string host = argv[1];
    short port = atoi(argv[2]);
    
    ConnectionHandler* connectionHandler = new ConnectionHandler(host, port);
    if (!connectionHandler->connect()) {
        std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
        return 1;
    }
	
	ServerTask task(*connectionHandler);

	boost::thread t1(&ServerTask::runKeyboardInput, &task);
	boost::thread t2(&ServerTask::runServerInput, &task);

	t1.join();
	t2.join();

	delete connectionHandler;

    return 0;
}
