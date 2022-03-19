package cs455.scaling;

import java.util.Timer;
import java.util.TimerTask;

public class Scheduler extends Thread{
    private final int batchTime;
    private Server server;

    public Scheduler(int batchTime, Server server){
        this.batchTime = batchTime;
        this.server = server;
    }

    @Override
    public void run() {
        Timer timerPrint = new Timer();
        Timer timerBatch = new Timer();
        ServerStats stats = new ServerStats();
        long batchTimeLong = batchTime;
        
        timerPrint.scheduleAtFixedRate(stats, 0L, 5300L);
        timerBatch.scheduleAtFixedRate(new TimerTask() {
            public void run(){
                server.resetBatch();
            }
        }, 0L, batchTimeLong );
        
        
    }
    
}
