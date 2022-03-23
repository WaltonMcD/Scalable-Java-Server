package cs455.scaling;
import java.io.IOException;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;

import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ReadAndRespond implements Runnable{
    public HashUtility hashUtil = new HashUtility();
    private ByteBuffer buffer;
    private final SocketChannel client;
    private static final AtomicLong numMessagesDone = new AtomicLong(0);
    private static final HashMap<SocketChannel, AtomicInteger> clientSentMessages = new HashMap<>();

    public ReadAndRespond(SelectionKey key){
        this.buffer = ByteBuffer.allocate(1024); // to store the messages
        this.client = (SocketChannel) key.channel(); // get the right client 
    }

    public synchronized void run() {
            try{
                int flag = client.read(buffer); // read the buffer
                if(flag == 0){
                    return;
                }

                if (!clientSentMessages.containsKey(client)) {
                    clientSentMessages.put(client, new AtomicInteger(1));
                } else {
                    // socketChannel already exists
                    AtomicInteger numMessagesSent = clientSentMessages.get(client);
                    numMessagesSent.getAndIncrement();
                }

                byte[] recvBytes = buffer.array(); // receive the messages
                buffer.clear();
                buffer.flip();

                String hash = hashUtil.SHA1FromBytes(recvBytes); // get the hash to send back to the client
                buffer = ByteBuffer.wrap(hash.getBytes());
                client.write(buffer);
                buffer.clear();
                numMessagesDone.getAndIncrement();
            }
            catch(IOException ioe){
                ioe.printStackTrace();
            }
            
        
    }

    public static AtomicLong getNumMessagesDone() {
        return numMessagesDone;
    }

    public static void resetNumMessagesDone() {
        numMessagesDone.set(0);
    }
    public static HashMap<SocketChannel, AtomicInteger> getClientNumSentMessages () {
        return clientSentMessages;
    }

    public static void clearHashMap(){
        for(AtomicInteger temp : clientSentMessages.values()){
            temp.set(0);
        }
    }

}