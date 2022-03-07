package cs455.scaling;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

public class ThreadPoolManager {
    private final Thread[] workerThreads;
    private final LinkedBlockingQueue<ReadAndRespond> workerQueue;

    public ThreadPoolManager(int threadCount){
        this.workerThreads = new Thread[threadCount];
        this.workerQueue = new LinkedBlockingQueue<ReadAndRespond>();
        int id = 0;
        for(Thread thread: workerThreads){
            thread = new worker(++id);
            thread.start();
        }
    }

    public void addTask(ReadAndRespond runner){
        try {
            workerQueue.put(runner);
        } catch (InterruptedException e) {
            System.out.println("Manager Add Task: " + e.getMessage());
        }
    }

    class worker extends Thread{
        private final int identifier;
        
        public worker(int identifier){
            this.identifier = identifier;
        }

        @Override
        public void run(){
            while(true){
                try {
                    if(!workerQueue.isEmpty()){
                        workerQueue.poll().readAndRespond();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
}
