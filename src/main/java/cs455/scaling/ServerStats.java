package cs455.scaling;

import java.lang.management.ThreadInfo;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ServerStats extends TimerTask{
    
    public void run(){

        AtomicInteger numClients = Server.getNumClients();
        AtomicLong numMessagesProcessed = ReadAndRespond.getNumMessagesDone();
        double serverThroughput = numMessagesProcessed.get() / 20.0;
        HashMap<SocketChannel, AtomicInteger> clientNumSentMessages = ReadAndRespond.getClientNumSentMessages();
        ArrayList<Double> throughputs = new ArrayList<>();
        double sumThroughPut = 0.0;

        for(AtomicInteger temp : clientNumSentMessages.values()){
            double individualThroughput = temp.get()/20.0;
            throughputs.add(individualThroughput);
            sumThroughPut+=individualThroughput;
        }

        double mean = sumThroughPut/clientNumSentMessages.size();
            // sd = sqrt( (sum( individual - mean) ^2 ) / N)
        double sumOfMeanMinusIndividual = 0.0;
        for(Double individulThroughput : throughputs){
            sumOfMeanMinusIndividual += Math.pow(individulThroughput - mean, 2.0);
        } 
        double squareThis = sumOfMeanMinusIndividual / clientNumSentMessages.size();
        double standardDeviation = Math.sqrt(squareThis);

        System.out.print("ServerThroughput: " + serverThroughput + "Message/s" + 
                            "Active Client Connections: " + numClients +
                            " Mean Perclient Throughput: " + mean + " messages/s" + 
                            "Std. Dev. Of Per-client Throughput: " + standardDeviation + "messages/s");




    }
}
