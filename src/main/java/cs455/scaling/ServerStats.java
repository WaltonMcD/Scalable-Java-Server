package cs455.scaling;

import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ServerStats {
    
    public void getServerStats(){

        AtomicInteger numClients = Server.getNumClients();
        AtomicLong numMessagesProcessed = ReadAndRespond.getNumMessagesDone();
        double serverThroughput = numMessagesProcessed.get() / 20.0;
        HashMap<SocketChannel, AtomicInteger> clientNumSentMessages = ReadAndRespond.getClientNumSentMessages();




    }
}
