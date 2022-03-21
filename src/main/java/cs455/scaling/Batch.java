package cs455.scaling;

import java.util.concurrent.LinkedBlockingQueue;

public class Batch {
    private volatile LinkedBlockingQueue<Runnable> batch;

    public Batch(){
        batch = new LinkedBlockingQueue<>();
    }

    public void addTask(Runnable task){
        batch.add(task);
    }
    
    public int getSize(){
        return batch.size();
    }
    
    public LinkedBlockingQueue<Runnable> getBatch(){
        return batch;
    }
}
