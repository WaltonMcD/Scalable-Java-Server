package cs455.scaling;

import java.net.InetSocketAddress;
import java.io.IOException;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import java.util.Iterator;
import java.util.Set;

public class Server {
    private Selector selector;
    private ServerSocketChannel serverSocket;
    private ThreadPoolManager threadPoolManager;
    
    public Server(String hostName, int portNum, int threadCount) throws IOException {
        this.selector = Selector.open();
        this.serverSocket = ServerSocketChannel.open();
        
        this.serverSocket.bind( new InetSocketAddress(hostName, portNum));
        this.serverSocket.configureBlocking(false);
        this.serverSocket.register(selector, SelectionKey.OP_ACCEPT);

        this.threadPoolManager = new ThreadPoolManager(threadCount);
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


                if(key.isReadable()){ // Checks if current clients is acceptable key has a value to read.
                    ReadAndRespond readRes = new ReadAndRespond(key);
                    threadPoolManager.addTask(readRes);
                }

                iter.remove(); // dont read the same message twice
            }
        }
        }
        catch(IOException ioe){
            System.out.println("Server Error: " + ioe.getMessage());
        }
    }

    private void register(Selector selector, ServerSocketChannel serverSocket) throws IOException {
        SocketChannel client = this.serverSocket.accept();

        client.configureBlocking(false);
        client.register(this.selector, SelectionKey.OP_READ);
        System.out.println("\t\tNew Client Registered... ");
    }

    
}
