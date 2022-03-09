package cs455.scaling;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

public class ThreadPoolManager {
    private final Thread[] workerThreads;
    private final LinkedBlockingQueue<Batch> workerQueue;

    public ThreadPoolManager(int threadCount){
        this.workerThreads = new Thread[threadCount];
        this.workerQueue = new LinkedBlockingQueue<Batch>();
        int id = 0;
        for(Thread thread: workerThreads){
            thread = new worker(++id);
            thread.start();
        }
    }

    public void addTask(Batch batch){
        try {
            workerQueue.put(batch);
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
                        Batch aBatch =  workerQueue.take();
                        for(ReadAndRespond eachMessage: aBatch.getBatch())
                            eachMessage.readAndRespond();
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
}
