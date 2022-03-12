package cs455.scaling;
import java.io.IOException;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;

import java.nio.channels.SocketChannel;
import java.util.HashMap;

public class ReadAndRespond {
    public HashUtility hashUtil = new HashUtility();
    private ByteBuffer buffer;
    private final SocketChannel client;
    private static final HashMap<SocketChannel, Integer> clientSentMessages = new HashMap<>();

    public ReadAndRespond(SelectionKey key){
        this.buffer = ByteBuffer.allocate(1024); // to store the messages
        this.client = (SocketChannel) key.channel(); // get the right client 
        clientSentMessages.put(client,clientSentMessages.getOrDefault(client, 0) + 1); // Start with 1. Then increment with each message
    }

    public synchronized void readAndRespond() {
            try{
                int flag = client.read(buffer); // read the buffer
                if(flag == 0){
                    return;
                }
                byte[] recvBytes = buffer.array(); // receive the messages
                buffer.clear();

                buffer = ByteBuffer.allocate(40);
                System.out.println(recvBytes);
                String hash = hashUtil.SHA1FromBytes(recvBytes); // get the hash to send back to the client
                System.out.println(hash);
                System.out.println("\t\tReceived: " + hash);

                buffer = ByteBuffer.wrap(hash.getBytes());
                client.write(buffer);
                buffer.clear();
                
            }
            catch(IOException ioe){
                ioe.printStackTrace();
            }
            
        
    }

}