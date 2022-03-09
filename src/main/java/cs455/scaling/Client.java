package cs455.scaling;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.Random;

public class Client {
    private SocketChannel client;
    private ByteBuffer buffer;
    private LinkedList<String> hashList;
    private int messageRate;
    HashUtility hashUtil = new HashUtility();

    public Client(String hostName, int portNum, int messageRate) throws IOException {
        this.client = SocketChannel.open(new InetSocketAddress(hostName, portNum));
        this.buffer = ByteBuffer.allocate(1024);
        this.hashList = new LinkedList<String>();
        this.messageRate = messageRate;
    }

   
    
    private byte[] getRandomBytes(){
        Random random = new Random();
        byte[] randomBytes = new byte[1024];
        random.nextBytes(randomBytes);
        return randomBytes;
    }

    private void createAndLinkHash(byte[] bytes){
        String hash = hashUtil.SHA1FromBytes(bytes);
        hashList.add(hash);
        System.out.println("Sent: " + hash);
        System.out.println("List Size: " + hashList.size());
    }

    private void checkAndRemoveHash(String hash){
        if(!this.hashList.contains(hash)){
            System.out.println("problem");
        }

        if(this.hashList.contains(hash)){
            hashList.remove(hash);
        }
        System.out.println("List Size: " + hashList.size());
    }

    public void start(){

        while(true){
            byte[] randomBytes = getRandomBytes();      //The random bytes that needs to be sent to the server
            createAndLinkHash(randomBytes);             // create hash and store locally
            buffer = ByteBuffer.wrap(randomBytes);      // wrap/ load bytes to send to server
    
            try{
                client.write(buffer);                   //write random bytes to the server
                //buffer.clear();                         //clear the buffer so that we can read later
                buffer = ByteBuffer.allocate(40); 
                client.read(buffer);                    //read the buffer          //convert read stuff to array an store in recv
                String recvHash = new String(buffer.array(), StandardCharsets.UTF_8); //create hash in client to crosscheck with what the server sent 
                //String recvHash = recv.substring(0,40);
                System.out.println("Client Received: " + recvHash);
                checkAndRemoveHash(recvHash);           // does what the methods says
                buffer.clear();
                Thread.sleep(1000/messageRate);
            }  catch(IOException ioe){
                System.err.println("Client Error: " + ioe.getMessage());
            } catch(InterruptedException ie){
                System.err.println("Client failed to sleep: " + ie.getMessage());
            }
        }
        
    }
}
