package chat.server;
import chat.network.TCPConnection;
import chat.network.TCPConnectionListener;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class ServerChat implements TCPConnectionListener {

    public static void main(String[] args) {
        new ServerChat();

    }

    private final ArrayList<TCPConnection> connections = new ArrayList<>();

    private ServerChat(){
        System.out.println("Server running");
        try(ServerSocket serverSocket = new ServerSocket(8189)){
            while(true){
                try{
                    new TCPConnection(this, serverSocket.accept());
                }catch (IOException e){
                    System.out.println(
                            "TCPConnection exception: " + e
                    );
                }
            }
        }catch (IOException e){
            throw new RuntimeException(e);
        }


    }

    @Override
    public void onConnectionReady(TCPConnection tcpConnection) {
        connections.add(tcpConnection);
        sendToAllClients("Client connected: " + tcpConnection);
    }

    @Override
    public void onReceiveString(TCPConnection tcpConnection, String value) {
        sendToAllClients(value);
    }

    @Override
    public void onDisconnect(TCPConnection tcpConnection) {
        connections.remove(tcpConnection);
        sendToAllClients("Client disconnected " + tcpConnection);
    }

    @Override
    public void onException(TCPConnection tcpConnection, Exception e) {
        System.out.println("TCPConnection exception: " + e);
    }
    
    private void sendToAllClients(String value){
        System.out.println(value);
        final int connectionSize = connections.size();
        for (int i = 0; i < connectionSize; i++) connections.get(i).sendString(value);
    }
}
