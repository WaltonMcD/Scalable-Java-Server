package cs455.scaling;
import java.io.IOException;
import java.math.BigInteger;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;

import java.nio.channels.SocketChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class ReadAndRespond {
    private ByteBuffer buffer;
    private final SocketChannel client;

    public ReadAndRespond(SelectionKey key){
        this.buffer = ByteBuffer.allocate(1024); // to store the messages
        this.client = (SocketChannel) key.channel(); // get the right client 
    }
 
    public String SHA1FromBytes(byte[] data) { 
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

    public void readAndRespond() throws IOException {

        int bytes = client.read(buffer); // read the buffer
        if(bytes == 1){  //checks for closing protocol
            client.close();
            System.out.println("\t\tClient Has Disconnected... \n");
        }
        else{
            byte[] recvBytes = buffer.array(); // receive the messages
            buffer.clear();
            String hash = new String(SHA1FromBytes(recvBytes)); // get the hash to send back to the client
            System.out.println("\t\tReceived: " + hash);

            buffer = ByteBuffer.wrap(hash.getBytes());

            client.write(buffer);
            buffer.clear();
        }
    }


}