package cs455.scaling;

public class ThreadPoolManager {
    private final Thread[] workerThreads;

    public ThreadPoolManager(int threadCount){
        this.workerThreads = new Thread[threadCount];
        
    }
    
}
