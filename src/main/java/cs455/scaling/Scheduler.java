package cs455.scaling;

import java.util.Timer;

public class Scheduler extends Thread{
    private final int batchTime;

    public Scheduler(int batchTime){
        this.batchTime = batchTime;
    }

    @Override
    public void run() {
        Timer timer = new Timer();
        ServerStats stats = new ServerStats();

        timer.scheduleAtFixedRate(stats, 0L, 5300L);
    }
    
}
