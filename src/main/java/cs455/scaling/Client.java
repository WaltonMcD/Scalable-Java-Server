package main.java.cs455.scaling;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Client {
    private SocketChannel client;
    private ByteBuffer buffer;

    public Client(String hostName, int portNum) throws IOException {
        this.client = SocketChannel.open(new InetSocketAddress(hostName, portNum));
        this.buffer = ByteBuffer.allocate(256);
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
        }
        catch(IOException ioe){
            System.out.println("Client: " + ioe.getMessage());
        }
    }
}
