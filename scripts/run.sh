#! /bin/bash

USAGE="
  USAGE:
  ./run.sh [OPTION] [OPTIONAL_ARGS]
  Options:
    deploy: run 1 server and specified number of clients on hosts specified in hostmachines.txt. If no arguments are provided defaults will be used.
      Ex sh scripts/run.sh deploy [SERVER_PORT=8000] [THREAD_POOL_SIZE=10] [BATCH_SIZE=10] [MESSAGE_RATE=4] [NUMBER_OF_CLIENTS=100]
    server: run 1 server on localhost. If no arguments are provided defaults will be used.
      Ex sh scripts/run.sh server [SERVER_PORT=8000] [THREAD_POOL_SIZE=10] [BATCH_SIZE=10]
    client: run 1 client on localhost. If no arguments are provided defaults will be used.
      Ex sh/run.sh client [SERVER_HOST=localhost] [SERVER_PORT=8000] [MESSAGE_RATE=4]
    clean: Kills tmux sessions on machines listed in current_machines.txt (logged by deploy option)
    ssh:  set up ssh keys between all hosts in hostmachines.txt\n"

if [[ $# < 1 ]]; then
    echo -e "\nERROR: too few args provided.\n$USAGE"
    exit 1
fi

if [[ $# = "help" ]]; then
    echo -e "$USAGE"
    exit 1
fi

machines=()
readarray -t machines < ./scripts/hostmachines.txt

if [[ "$1" = "ssh" ]]; then
    echo "Setting up SSH to connection to: " ${machines[*]}
    ssh-keygen
    for machine in ${machines[@]}; do
      ssh-copy-id ${machine}
    done
    for machine in ${machines[@]}; do
      ssh -n ${machine} "uname -a"
    done
    exit 0
fi

if [[ $1 = "deploy" ]]; then
    gradle build
    SERVER_PORT=${2:-8000}
    THREAD_POOL_SIZE=${3:-10}
    BATCH_SIZE=${4:-10}
    MESSAGE_RATE=${4:-4}
    NUMBER_OF_CLIENTS=${5:-100}

    PROJECT_DIR=$(pwd)
    echo $PROJECT_DIR
    BASE_CMD="cd ${PROJECT_DIR} && sh ./scripts/run.sh"
    SERVER=${machines[0]}
    CLIENTS=${machines[@]:1:NUMBER_OF_CLIENTS}

    #save to file so kill option knows which hosts to kill tmux sessions on
    echo -e "$SERVER\n$CLIENTS" > scripts/current_tmux_sessions.txt
    
    echo "Server: " ${machines[0]}
    ssh ${SERVER} "tmux new -d -s ${SERVER} '${BASE_CMD} server ${SERVER_PORT} ${THREAD_POOL_SIZE} ${BATCH_SIZE}' "

    echo "Clients: " ${CLIENTS[@]}
    i=1
    for machine in ${CLIENTS[@]}; do
      echo "creating client session ${i} on ${machine}..."
      ssh ${machine} "tmux new -d -s '${machine}-${i}' '${BASE_CMD} client ${SERVER} ${SERVER_PORT} ${MESSAGE_RATE}' "
      let i=i+1
    done
    exit 0
fi  

if [[ "$1" = "clean" ]]; then
    machines=()
    readarray -t machines < ./scripts/current_tmux_sessions.txt
    for machine in ${machines[@]}; do
      echo "Killing tmux on ${machine}"
      ssh ${machine} "tmux kill-server > /dev/null 2>&1"
    done
    rm scripts/current_tmux_sessions.txt
    exit 0
fi

CLASS_NAME=$1
BASE_COMMAND="java -jar build/libs/Homework-2.jar cs455.scaling.Main"

if [[ $CLASS_NAME = "server" ]]; then
    SERVER_PORT=${2:-8000}
    THREAD_POOL_SIZE=${3:-10}
    BATCH_SIZE=${4:-10}
    echo "Starting Server..."
    $BASE_COMMAND server $SERVER_PORT $THREAD_POOL_SIZE $BATCH_SIZE
fi

if [[ $CLASS_NAME = "client" ]]; then
    SERVER_HOST=${2:-localhost}
    SERVER_PORT=${3:-8000}
    MESSAGE_RATE=${4:-4}
    echo "Starting Client..."
    $BASE_COMMAND client $SERVER_HOST $SERVER_PORT $MESSAGE_RATE
fi
