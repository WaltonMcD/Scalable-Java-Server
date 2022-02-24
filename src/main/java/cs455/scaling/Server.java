package main.java.cs455.scaling;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
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

    public void start() throws IOException {

        while(true){
            System.out.println("Listening For New Connections... ");

            //Blocking call
            this.selector.select();
            System.out.println("\tActivity On Selector.");

            Set<SelectionKey> selectedKeys = selector.selectedKeys();

            Iterator<SelectionKey> iter = selectedKeys.iterator();
            while(iter.hasNext()){
                SelectionKey key = iter.next();

                if(key.isValid() == false){
                    continue;
                }
                // isAcceptable checks for potential new clients
                if(key.isAcceptable()){
                    register(this.selector, this.serverSocket);
                }

                if(key.isReadable()){
                    readAndRespond(key);
                }

                iter.remove();
            }
        }
        
    }

    private void register(Selector selector, ServerSocketChannel serverSocket) throws IOException {
        SocketChannel client = this.serverSocket.accept();

        client.configureBlocking(false);
        client.register(this.selector, SelectionKey.OP_READ);
        System.out.println("New Client Registered... ");
    }

    private void readAndRespond(SelectionKey key) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(256);

        SocketChannel client = (SocketChannel) key.channel();

        int bytes = client.read(buffer);
        if(bytes == 1){
            client.close();
            System.out.println("Client Has Disconnected... ");
        }
        else{
            String response = new String(buffer.array());
            System.out.println("Received: " + response);

            buffer.flip();
            client.write(buffer);

            buffer.clear();
        }
    }
}
