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
        this.buffer = ByteBuffer.allocate(8000);
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
        byte[] randomBytes = new byte[8000];
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
        byte[] randomBytes = getRandomBytes();
        createAndLinkHash(randomBytes);
        buffer = ByteBuffer.wrap(randomBytes);

        try{
            client.write(buffer);
            buffer.clear();

            client.read(buffer);
            byte[] recv = buffer.array();
            String recvHash = new String(SHA1FromBytes(recv));
            checkAndRemoveHash(recvHash);

            buffer.clear();
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
