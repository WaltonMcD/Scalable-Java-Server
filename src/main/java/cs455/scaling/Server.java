package cs455.scaling;

import java.net.InetSocketAddress;
import java.io.IOException;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class Server {
    private Selector selector;
    private ServerSocketChannel serverSocket;
    private ThreadPoolManager threadPoolManager;
    private int batchSize;
    private int batchTime;
    Batch batch;
    private static AtomicInteger numClients = new AtomicInteger(0);

    
    public Server(String hostName, int portNum, int threadCount, int batchSize, int batchTime) throws IOException {
        this.selector = Selector.open();
        this.serverSocket = ServerSocketChannel.open();
        this.serverSocket.bind( new InetSocketAddress(hostName, portNum));
        this.serverSocket.configureBlocking(false);
        this.serverSocket.register(selector, SelectionKey.OP_ACCEPT);

        this.threadPoolManager = new ThreadPoolManager(threadCount);
        this.batch = new Batch();
        this.batchSize = batchSize;
        this.batchTime = batchTime;
    }

    public void start() {
        try{
             
            Thread thread = new Scheduler(batchTime, this);
            thread.start();
            while(true){

                int flag = this.selector.select();  //Blocking call. 
                if(flag != 0){
                    continue;
                }                             

                Set<SelectionKey> selectedKeys = selector.selectedKeys(); // set of all received types of messages 

                Iterator<SelectionKey> iter = selectedKeys.iterator(); //make the messages iterable
                while(iter.hasNext()){
                    SelectionKey key = iter.next();                    // get the key
                    
                    if(key.isValid() == false)              // corner case 
                        continue;

                    if(key.isAcceptable()) // isAcceptable checks for potential new clients
                        if(key.attachment() == null){
                            Register register = new Register(this.selector, this.serverSocket, 42); // registers the client to the server
                            batch.addTask(register);

                            if(batch.getSize()==batchSize){
                                threadPoolManager.addTask(batch);
                                resetBatch();
                            }
                        }
                        
                    if(key.isReadable()){ // Checks if current clients is acceptable key has a value to read.
                        if(key.attachment() != null){
                            ReadAndRespond readRes = new ReadAndRespond(key);
                            batch.addTask(readRes);

                            if(batch.getSize()==batchSize){
                                threadPoolManager.addTask(batch);
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
    
    public void resetBatch(){
        this.batch = new Batch();
    }

    public static AtomicInteger getNumClients() {
        return numClients;
    }

    
}
