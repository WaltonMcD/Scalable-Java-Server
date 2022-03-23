Team:
Kiera Jost
Videep Venkatesha
Walton McDonald

Execution:
sh scripts/run.sh [OPTION] [OPTIONAL_ARGS]
For 1 server and 100 clients (port 8000, thread pool size of 10, batch size of 10, batch time of 10, and message rate of 4):
sh scripts/run.sh deploy

After executing the script you may ssh to any of the displayed machines and simply type `tmux a` to view the session.

Other Options:
deploy: run 1 server and specified number of clients on hosts specified in hostmachines.txt. If no arguments are provided defaults will be used.
sh scripts/run.sh deploy [SERVER_PORT=8000] [THREAD_POOL_SIZE=10] [BATCH_SIZE=10] [BATCH_TIME=10] [NUMBER_OF_CLIENTS=100] [MESSAGE_RATE=4]
example: sh scripts/run.sh deploy 11235 10 10 10 20 4 

server: run 1 server on localhost. If no arguments are provided defaults will be used.
sh scripts/run.sh server [SERVER_PORT=8000] [THREAD_POOL_SIZE=10] [BATCH_SIZE=10] [BATCH_TIME=10]
example: sh scripts/run.sh server 11235 10 10 10

client: run 1 client on localhost. If no arguments are provided defaults will be used.
sh scripts/run.sh client [SERVER_HOST=localhost] [SERVER_PORT=8000] [MESSAGE_RATE=4]
example: sh scripts/run.sh client 11235 4

clean: Kills tmux sessions on machines listed in current_machines.txt (logged by deploy option)
example: sh scripts/run.sh clean

ssh: set up ssh keys between all hosts in hostmachines.txt\n"

File Manifest:
Homework-2/README.txt
Homework-2/build.gradle

Homework-2/scripts/current_tmux_sessions.txt
Homework-2/scripts/hostmachines.txt
Homework-2/scripts/run.sh

Homework-2/src/main/java/cs455/scaling/Main.java
Homework-2/src/main/java/cs455/scaling/Server.java
Homework-2/src/main/java/cs455/scaling/Client.java
Homework-2/src/main/java/cs455/scaling/Batch.java
Homework-2/src/main/java/cs455/scaling/HashUtility.java
Homework-2/src/main/java/cs455/scaling/ReadAndRespond.java
Homework-2/src/main/java/cs455/scaling/Register.java
Homework-2/src/main/java/cs455/scaling/Scheduler.java
Homework-2/src/main/java/cs455/scaling/ServerStats.java
Homework-2/src/main/java/cs455/scaling/ThreadPoolManager.java
