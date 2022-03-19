package cs455.scaling;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class Register implements Runnable{
    private Selector selector;
    private ServerSocketChannel serverSocket;
    private int object;

    public Register(Selector selector, ServerSocketChannel serverSocket, int object){
        this.selector = selector;
        this.serverSocket = serverSocket;
        this.object = object;
    }

    @Override
    public void run() {
        try{
            SocketChannel client = this.serverSocket.accept();
            if(client == null){
                return;
            }
            client.configureBlocking(false);
            client.register(this.selector, SelectionKey.OP_READ, object);
        }
        catch( IOException e){
            e.printStackTrace();
        }
        
        
    }
    
}
