import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.*;
import java.awt.BorderLayout;


public class chat_server extends JFrame {

    private ObjectOutputStream output;
    private ObjectInputStream input;
    private Socket connection;
    private ServerSocket server;
    private int port = 8888;
    
    private JTextArea chatArea;
    private JTextField jTextField1;
    private JButton jButton1;
    private JLabel status;
    
    public chat_server() {
        initComponents();
        this.setTitle("Chat-Server");
        this.setVisible(true);
    }
    
    private void initComponents() {
        chatArea = new JTextArea();
        jTextField1 = new JTextField();
        jButton1 = new JButton();
        status = new JLabel();
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
   
        setLayout(new BorderLayout());
        
        chatArea.setEditable(false);
        add(new JScrollPane(chatArea), BorderLayout.CENTER);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(jTextField1, BorderLayout.CENTER);
        jButton1.setText("Send");
        panel.add(jButton1, BorderLayout.EAST);
        
        add(panel, BorderLayout.SOUTH);
        add(status, BorderLayout.NORTH);
        
        jTextField1.addActionListener(evt -> {
            sendMessage(jTextField1.getText());
            jTextField1.setText("");
        });
        
        jButton1.addActionListener(evt -> {
            sendMessage(jTextField1.getText());
            jTextField1.setText("");
        });
    }

    public void startRunning() {
        try {
            server = new ServerSocket(port, 1); // Only one client
            try {
                status.setText("Waiting for Someone to Connect...");
                connection = server.accept(); // establishes connection and waits for the client 
                status.setText("Now Connected to " + connection.getInetAddress().getHostName());

                output = new ObjectOutputStream(connection.getOutputStream());
                output.flush();
                input = new ObjectInputStream(connection.getInputStream());

                whileChatting();

            } catch (EOFException eofException) {
                status.setText("Connection ended");
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
    
    private void whileChatting() throws IOException {
        String message = "";    
        jTextField1.setEditable(true);
        do {
            try {
                message = (String) input.readObject();
                chatArea.append("\n" + message);
            } catch (ClassNotFoundException classNotFoundException) {
                chatArea.append("\nUnknown message format");
            }
        } while (!message.equals("Bye"));
    }

    private void sendMessage(String message) {
        try {
            chatArea.append("\nME(Server) - " + message);
            output.writeObject("Server - " + message);
            output.flush();
        } catch (IOException ioException) {
            chatArea.append("\n Unable to Send Message");
        }
    }

    public static void main(String[] args) {
        chat_server server = new chat_server();
        server.startRunning();
    }
}
