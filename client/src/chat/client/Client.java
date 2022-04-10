package chat.client;

import chat.network.TCPConnection;
import chat.network.TCPConnectionListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class Client extends JFrame implements ActionListener, TCPConnectionListener {

    private static final String IP = "Your IP :-)";
    private static final int PORT = "Your port";
    private static final int WIDTH = 600;
    private static final int HEIGHT = 400;
    private final JTextArea log = new JTextArea();
    private final JTextField fieldName = new JTextField("John");
    private final JTextField inputField = new JTextField();
    private TCPConnection connection;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Client();
            }
        });

    }

    private Client() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);
        setResizable(false);
        log.setEnabled(false);
        log.setLineWrap(true);
        add(log, BorderLayout.CENTER);
        inputField.addActionListener(this);
        add(inputField, BorderLayout.SOUTH);
        add(fieldName, BorderLayout.NORTH);
        setVisible(true);
        try {
            connection = new TCPConnection(this, IP, PORT);
        } catch (IOException e) {
            printMessage("Connection exception: " + e);
        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String msg = inputField.getText();
        if (msg.equals("")) return;
        inputField.setText(null);
        connection.sendString(fieldName.getText() + ": " + msg);
    }

    @Override
    public void onConnectionReady(TCPConnection tcpConnection) {
        printMessage("Connection is ready");
    }

    @Override
    public void onReceiveString(TCPConnection tcpConnection, String value) {
        printMessage(value);
    }

    @Override
    public void onDisconnect(TCPConnection tcpConnection) {
        printMessage("Connection closed");
    }

    @Override
    public void onException(TCPConnection tcpConnection, Exception e) {
        printMessage("Connection exception: " + e);
    }

    private synchronized void printMessage(String msg) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                log.append(msg + "\n");
                log.setCaretPosition(log.getDocument().getLength());
            }
        });
    }
}
