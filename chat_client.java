import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import javax.swing.*;
import java.awt.BorderLayout;

public class chat_client extends JFrame {

    private ObjectOutputStream output;
    private ObjectInputStream input;
    private Socket connection;
    private String serverIP;
    private int port = 8888;
    
    private JTextArea chatArea;
    private JTextField jTextField1;
    private JButton jButton1;
    
    public chat_client(String host) {
        serverIP = host;
        initComponents();
        this.setTitle("Chat-Client");
        this.setVisible(true);
    }
    
    private void initComponents() {
        chatArea = new JTextArea();
        jTextField1 = new JTextField();
        jButton1 = new JButton();
        
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
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
            connection = new Socket(serverIP, port);
           output = new ObjectOutputStream(connection.getOutputStream());
        output.flush();
        input = new ObjectInputStream(connection.getInputStream());
        chatArea.append("Connected to: " + connection.getInetAddress().getHostName() + "\n");

            whileChatting();
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
            chatArea.append("\nME(Client) - " + message);
            output.writeObject("Client - " + message);
            output.flush();
        } catch (IOException ioException) {
            chatArea.append("\n Unable to Send Message");
        }
    }

    public static void main(String[] args) {
        chat_client client = new chat_client("127.0.0.1"); // Replace with server's IP address
        client.startRunning();
    }
}
