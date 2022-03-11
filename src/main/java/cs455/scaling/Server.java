package cs455.scaling;

import java.net.InetSocketAddress;
import java.io.IOException;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import java.util.Iterator;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class Server {
    private Selector selector;
    private ServerSocketChannel serverSocket;
    private ThreadPoolManager threadPoolManager;
    private int batchSize;
    private int batchTime;
    Batch currentBatch;
    boolean batchSizeReached = false;
    Object lock;
    
    public Server(String hostName, int portNum, int threadCount, int batchSize, int batchTime) throws IOException {
        this.selector = Selector.open();
        this.serverSocket = ServerSocketChannel.open();
        this.batchSize = batchSize;
        this.batchTime = batchTime;
        this.serverSocket.bind( new InetSocketAddress(hostName, portNum));
        this.serverSocket.configureBlocking(false);
        this.serverSocket.register(selector, SelectionKey.OP_ACCEPT);

        this.threadPoolManager = new ThreadPoolManager(threadCount);
        currentBatch = new Batch();
       
    }
    public class Intervals extends Thread{
        public void run(){
            while(true){
                long test = System.currentTimeMillis();
                if(test >= (batchTime * 1000)) { //multiply by 1000 to get milliseconds
                    synchronized (lock){
                        if(currentBatch.getSize()>0){
                            resetBatch();
                            System.out.print("called by time");
                         }
                    }
                }
            }
        }
    }

    public void start() {
        try{
            Intervals obj = new Intervals();
            Thread thread = obj;
            thread.start();

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
                        if(key.attachment() == null){
                            register(this.selector, this.serverSocket, 42); // registers the client to the server
                        }
                        
                    if(key.isReadable()){ // Checks if current clients is acceptable key has a value to read.
                        if(key.attachment() != null){
                            ReadAndRespond readRes = new ReadAndRespond(key);
                            currentBatch.addTask(readRes);
                            
                            if(currentBatch.getSize()==batchSize){
                                resetBatch();
                            }
                        }      
                    }
                    iter.remove(); // dont read the same message twice
                }
            }
        }
        catch(IOException ioe){
            System.out.println("Server Error: " + ioe.getMessage());
        }
    }

    private void register(Selector selector, ServerSocketChannel serverSocket, int object) throws IOException {
        SocketChannel client = this.serverSocket.accept();

        client.configureBlocking(false);
        client.register(this.selector, SelectionKey.OP_READ, object);
        System.out.println("\t\tNew Client Registered... ");
    }
    
   
    
        private void resetBatch(){
        threadPoolManager.addTask(currentBatch);
        currentBatch = new Batch();
       
        }
    

    
}
