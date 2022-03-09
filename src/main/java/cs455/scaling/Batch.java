package cs455.scaling;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

public class Batch {
    private volatile LinkedBlockingQueue<ReadAndRespond> batch;

    public Batch(){
        batch = new LinkedBlockingQueue<>();
    }

    public void addTask(ReadAndRespond task){
        batch.add(task);
    }
    
    public int getSize(){
        return batch.size();
    }
    public LinkedBlockingQueue<ReadAndRespond> getBatch(){
        return batch;
    }
}
