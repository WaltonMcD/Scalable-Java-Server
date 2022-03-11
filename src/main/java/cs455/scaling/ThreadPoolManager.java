package cs455.scaling;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

public class ThreadPoolManager extends Thread{
    private final Thread[] workerThreads;                  // The threads that perform the task
    private final LinkedBlockingQueue<Batch> batchQueue; //The actual list which will hold batches of tasks
    
    Batch currentBatch = new Batch();
    public ThreadPoolManager(int threadCount){
        this.workerThreads = new Thread[threadCount];
        this.batchQueue = new LinkedBlockingQueue<Batch>();
        int id = 0;
        
        for(Thread thread: workerThreads){
            thread = new worker(++id);
            thread.start();
        }
    }
  

    public void addTask(Batch batch){
        try {
            
            batchQueue.put(batch);                                     
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
                    if(!batchQueue.isEmpty()){
                        Batch aBatch =  batchQueue.take();
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
