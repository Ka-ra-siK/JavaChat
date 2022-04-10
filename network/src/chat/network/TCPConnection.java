package chat.network;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;

public class TCPConnection {
    private final Socket socket;
    private final Thread rxThread; // Поток входящих сообщений
    private final BufferedReader in;
    private final BufferedWriter out;
    private final TCPConnectionListener evenListener;

    public TCPConnection(TCPConnectionListener evenListener, String ip, int port) throws IOException{
        this(evenListener, new Socket(ip, port));
    }

    public TCPConnection(TCPConnectionListener evenListener, Socket socket) throws IOException {
        this.evenListener = evenListener;
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8")));
        rxThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    evenListener.onConnectionReady(TCPConnection.this);
                    while(!rxThread.isInterrupted()){
                        evenListener.onReceiveString(TCPConnection.this, in.readLine());
                    }
                    String msg = in.readLine();
                } catch (IOException e) {
                    evenListener.onException(TCPConnection.this, e);
                } finally {
                    evenListener.onDisconnect(TCPConnection.this);
                }

            }
        });
        rxThread.start();
    }

    public synchronized void sendString(String str){
        try {
            out.write(str + "\r\n");
            out.flush(); // сброс буфера
        } catch (IOException e) {
            evenListener.onException(TCPConnection.this, e);
            disconnect();
        }

    }

    public synchronized void disconnect(){
        rxThread.interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            evenListener.onException(TCPConnection.this, e);
        }

    }

    @Override
    public String toString(){ //логи
        return "TCPConnection: " + socket.getInetAddress() + ": " + socket.getPort(); // адрес + порт
    }

}
