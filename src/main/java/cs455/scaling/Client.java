package cs455.scaling;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Client {
    private SocketChannel client;
    private ByteBuffer buffer;

    public Client(String hostName, int portNum) throws IOException {
        this.client = SocketChannel.open(new InetSocketAddress(hostName, portNum));
        this.buffer = ByteBuffer.allocate(8000);
    }

    public String SHA1FromBytes(byte[] data) throws NoSuchAlgorithmException { 
        MessageDigest digest = MessageDigest.getInstance("SHA1"); 
        byte[] hash  = digest.digest(data); 
        BigInteger hashInt = new BigInteger(1, hash); 
     
        return hashInt.toString(16); 
    } 

    public void start(){
        String text = new String("Hey I'm gonna need this back.");
        buffer = ByteBuffer.wrap(text.getBytes());

        String response = null;

        try{
            client.write(buffer);
            buffer.clear();

            client.read(buffer);
            response = new String(buffer.array()).trim();
            System.out.println("Server response: " + response);

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
