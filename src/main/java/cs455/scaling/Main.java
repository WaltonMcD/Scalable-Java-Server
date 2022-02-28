package cs455.scaling;

import java.io.IOException;
import java.net.InetAddress;

public class Main {
    
    public static void main(String[] args) throws IOException {
        
        if(args[1].equals("server")){
            int portNum = Integer.parseInt(args[2]);
            String hostName = InetAddress.getLocalHost().getHostAddress();
            // int threadPoolSize = Integer.parseInt(args[3]);
            // int batchSize = Integer.parseInt(args[4]);
            // int batchTime = Integer.parseInt(args[5]);

            Server server = new Server(hostName, portNum);
            server.start();

        }
        else if(args[1].equals("client")){
            String serverHost = args[2];
            int serverPort = Integer.parseInt(args[3]);
            // int messageRate = Integer.parseInt(args[4]);

            Client client = new Client(serverHost, serverPort);
            client.start();
        }
    }
}
