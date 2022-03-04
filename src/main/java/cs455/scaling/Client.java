package cs455.scaling;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.Random;

public class Client {
    private SocketChannel client;
    private ByteBuffer buffer;
    private LinkedList<String> hashList;

    public Client(String hostName, int portNum) throws IOException {
        this.client = SocketChannel.open(new InetSocketAddress(hostName, portNum));
        this.buffer = ByteBuffer.allocate(1024);
        this.hashList = new LinkedList<String>();
    }

    private String SHA1FromBytes(byte[] data) { 
        BigInteger hashInt = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA1");
            byte[] hash  = digest.digest(data); 
            hashInt = new BigInteger(1, hash); 
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Error SHA1: " + e.getMessage());
        } 
        return hashInt.toString(16); 
    }
    
    private byte[] getRandomBytes(){
        Random random = new Random();
        byte[] randomBytes = new byte[1024];
        random.nextBytes(randomBytes);
        return randomBytes;
    }

    private void createAndLinkHash(byte[] bytes){
        String hash = SHA1FromBytes(bytes);
        hashList.add(hash);
        System.out.println("List Size: " + hashList.size());
    }

    private void checkAndRemoveHash(String hash){
        if(this.hashList.contains(hash)){
            hashList.remove(hash);
        }
        System.out.println("List Size: " + hashList.size());
    }

    public void start(){
        byte[] randomBytes = getRandomBytes();      //The random bytes that needs to be sent to the server
        createAndLinkHash(randomBytes);             // create hash and store locally
        buffer = ByteBuffer.wrap(randomBytes);      // wrap/ load bytes to send to server

        try{
            client.write(buffer);                   //write random bytes to the server
            buffer.clear();                         //clear the buffer so that we can read later

            client.read(buffer);                    //read the buffer
            byte[] recv = buffer.array();           //convert read stuff to array an store in recv
            String recvHash = new String(SHA1FromBytes(recv)); //create hash in client to crosscheck with what the server sent 
            checkAndRemoveHash(recvHash);           // does what the methods says

            buffer.clear();
            // closing connection protocol
            byte[] end = {1};
            buffer = ByteBuffer.wrap(end);

            client.write(buffer);

            client.close();
        }
        catch(IOException ioe){
            System.out.println("Client Error: " + ioe.getMessage());
        }
    }
}
