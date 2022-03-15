# Homework-2

---

### Cloning

- git clone https://github.com/WaltonMcD/Homework-2.git

---

### Gradle

#### To setup gradle with the current `build.gradle`

- `gradle build`

---

### Execute

`sh scripts/run.sh [OPTION] [OPTIONAL_ARGS]`

For 1 server and 100 clients (port 8000, thread pool size of 10, batch size of 10, and message rate of 4):

`sh scripts/run.sh deploy`

#### Other Options:

deploy: run 1 server and specified number of clients on hosts specified in hostmachines.txt. If no arguments are provided defaults will be used.

`sh scripts/run.sh deploy [SERVER_PORT=8000] [THREAD_POOL_SIZE=10] [BATCH_SIZE=10] [MESSAGE_RATE=4] [NUMBER_OF_CLIENTS=100]`

server: run 1 server on localhost. If no arguments are provided defaults will be used.

`sh scripts/run.sh server [SERVER_PORT=8000] [THREAD_POOL_SIZE=10] [BATCH_SIZE=10]`

client: run 1 client on localhost. If no arguments are provided defaults will be used.

`sh scripts/run.sh client [SERVER_HOST=localhost] [SERVER_PORT=8000] [MESSAGE_RATE=4]`

clean: Kills tmux sessions on machines listed in current_machines.txt (logged by deploy option)

ssh: set up ssh keys between all hosts in hostmachines.txt\n"
