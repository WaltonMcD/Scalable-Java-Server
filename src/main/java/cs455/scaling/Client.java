package cs455.scaling;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Client {
    private SocketChannel client;
    private ByteBuffer buffer;
    private LinkedList<String> hashList;
    private int messageRate;
    private int totalSentMessages;
    private int totalReceivedMessages;
    HashUtility hashUtil = new HashUtility();

    public Client(String hostName, int portNum, int messageRate) throws IOException {
        this.client = SocketChannel.open(new InetSocketAddress(hostName, portNum));
        this.buffer = ByteBuffer.allocate(1024);
        this.hashList = new LinkedList<String>();
        this.messageRate = messageRate;
        this.totalSentMessages = 0;
        this.totalReceivedMessages = 0;
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
    }

    private void checkAndRemoveHash(String hash){
        if(this.hashList.contains(hash)){
            hashList.remove(hash);
        }
    }

    public void start(){
        Timer timerPrint = new Timer();
        timerPrint.scheduleAtFixedRate(new TimerTask() {
                    public void run(){
                        System.out.println("Total Sent Count: " + totalSentMessages + " Total Received Count: " + totalReceivedMessages);
                    }
                }, 0L, 20000L );

        while(true){
            byte[] randomBytes = getRandomBytes();      //The random bytes that needs to be sent to the server
            createAndLinkHash(randomBytes);             // create hash and store locally
            buffer = ByteBuffer.wrap(randomBytes);      // wrap/ load bytes to send to server
    
            try{
                client.write(buffer);
                totalSentMessages++;                   //write random bytes to the server
                buffer.clear();                         //clear the buffer so that we can read later
                buffer.flip(); 
                client.read(buffer);
                totalReceivedMessages++;                    //read the buffer          //convert read stuff to array an store in recv
                String recv = new String(buffer.array(), StandardCharsets.UTF_8); //create hash in client to crosscheck with what the server sent 
                String recvHash = recv.substring(0,40);
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
