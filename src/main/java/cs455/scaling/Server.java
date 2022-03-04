package cs455.scaling;

import java.net.InetSocketAddress;
import java.io.IOException;
import java.math.BigInteger;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.util.Iterator;
import java.util.Set;

public class Server {
    private Selector selector;
    private ServerSocketChannel serverSocket;
    
    public Server(String hostName, int portNum) throws IOException {
        this.selector = Selector.open();
        this.serverSocket = ServerSocketChannel.open();
        
        this.serverSocket.bind( new InetSocketAddress(hostName, portNum));
        this.serverSocket.configureBlocking(false);
        this.serverSocket.register(selector, SelectionKey.OP_ACCEPT);
    }

    public void start() {
        try{
        while(true){
            System.out.println("Listening For New Connections... "); 

            
            this.selector.select();                             //Blocking call. 
            System.out.println("\tActivity On Selector... "); 

            Set<SelectionKey> selectedKeys = selector.selectedKeys(); // set of all received types of messages 

            Iterator<SelectionKey> iter = selectedKeys.iterator(); //make the messages iterable
            while(iter.hasNext()){
                SelectionKey key = iter.next();                    // get the key
                
                if(key.isValid() == false)              // corner case 
                    continue;

                
                if(key.isAcceptable()) // isAcceptable checks for potential new clients
                    register(this.selector, this.serverSocket); // registers the client to the server

                
                if(key.isReadable()) // Checks if current clients is acceptable key has a value to read.
                    readAndRespond(key);

                iter.remove(); // dont read the same message twice
            }
        }
        }
        catch(IOException ioe){
            System.out.println("Server Error: " + ioe.getMessage());
        }
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

    private void register(Selector selector, ServerSocketChannel serverSocket) throws IOException {
        SocketChannel client = this.serverSocket.accept();

        client.configureBlocking(false);
        client.register(this.selector, SelectionKey.OP_READ);
        System.out.println("\t\tNew Client Registered... ");
    }

    private void readAndRespond(SelectionKey key) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1024); // to store the messages

        SocketChannel client = (SocketChannel) key.channel(); // get the right client 

        int bytes = client.read(buffer); // read the buffer
        if(bytes == 1){  //checks for closing protocol
            client.close();
            System.out.println("\t\tClient Has Disconnected... \n");
        }
        else{
            byte[] recvBytes = buffer.array(); // receive the messages
            String hash = new String(SHA1FromBytes(recvBytes)); // get the hash to send back to the client
            System.out.println("\t\tReceived: " + hash);

            buffer.flip(); // flips read and write functinalooty
            
            client.write(buffer);
            buffer.clear();
        }
    }
}
