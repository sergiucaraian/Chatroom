package chatclient;

import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import org.json.JSONException;
import org.json.JSONObject;

public class ChatClient {
    private static InetAddress host;
    private static final int PORT = 1234;
    static Socket socket = null;
    
    public static void main(String[] args) {
        try {
            host = InetAddress.getLocalHost();
        }
        catch(UnknownHostException uhEx) {
            System.out.println("Host ID not found!");
            System.exit(1);
        }
        
        Thread guiThread = new Thread(new GUI());
        guiThread.start();
        
        try {
            socket = new Socket("192.168.0.100",PORT);
            
            Scanner inputStream = new Scanner(socket.getInputStream());
            PrintWriter outputStream = new PrintWriter(socket.getOutputStream(),true);
            Scanner localInput = new Scanner(System.in);
            
            while(!GUI.enteredUsername);
            String username = GUI.usernameField.getText();
            
            outputStream.println(JSONUtils.getAuthentication(username));
            
            try {
                String confirmation = inputStream.nextLine();
                JSONObject jObj = new JSONObject(confirmation);
                if(!jObj.getString("flag").equals("connected"))
                    throw new IllegalArgumentException("Rejected");
            } catch(Exception e) {
                System.out.println("Couldn't connect");
                e.printStackTrace();
            }
            
            Thread receiveThread = new Thread(new Receiver(socket,inputStream));
            receiveThread.start();
                
            String message = "";
                
            while(true) {
                if(GUI.sentMessage) {
                    message = JSONUtils.getMessage(username, GUI.inputTextField.getText());
                    outputStream.println(message);
                    GUI.inputTextField.setText("");
                    GUI.sentMessage = false;
                }
            }
        } catch(IOException ioEx) {
            ioEx.printStackTrace();
        }
        finally {
            try {
                System.out.println("\n* Closing connection… *");
                socket.close();
            }
            catch(IOException ioEx) {
                System.out.println("Unable to disconnect!");
                System.exit(1);
            }
        }
    }
}

class Receiver implements Runnable {
    Socket socket;
    Scanner inputStream;
    String message;
    
    Receiver(Socket socket, Scanner inputStream) {
        this.socket = socket;
        this.inputStream = inputStream;
    }
    public void run() {
        try {
            while(true) {
                JSONObject jMessage = new JSONObject(inputStream.nextLine());
                
                if(jMessage.getString("flag").equals("new")) {
                    GUI.chatTextArea.append(jMessage.getString("name") + " connected\n");
                }
                else if(jMessage.getString("flag").equals("exit")) {
                    GUI.chatTextArea.append(jMessage.getString("name") + " quit\n");
                }
                else if (jMessage.getString("flag").equals("message")) {
                    GUI.chatTextArea.append(jMessage.getString("name") + " > " + jMessage.getString("message") + "\n");
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}

class GUI extends JFrame implements ActionListener, Runnable {
    static JTextField usernameField;
    static JTextArea chatTextArea;
    static JTextField inputTextField;
    
    volatile static boolean enteredUsername = false;
    volatile static boolean sentMessage = false;
    static boolean pressedOnce = false;
    
    GUI() {
        usernameField = new JTextField(50);
        usernameField.setText("Enter username and press Enter...");
        add(usernameField, BorderLayout.NORTH);
        usernameField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                enteredUsername = true;
                usernameField.setEditable(false);
                inputTextField.setEditable(true);
            }
        });
        
        usernameField.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if(!pressedOnce)
                {
                    usernameField.setText("");
                    pressedOnce = true;
                }
            }
        });
        
        chatTextArea = new JTextArea(10,15);
        chatTextArea.setWrapStyleWord(true);
        chatTextArea.setLineWrap(true);
        chatTextArea.setEditable(false);
        //add(chatTextArea, BorderLayout.CENTER);
        
        JScrollPane scrollPane = new JScrollPane( chatTextArea );
        add(scrollPane, BorderLayout.CENTER );
        
        inputTextField = new JTextField(20);
        inputTextField.setEditable(false);
        inputTextField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sentMessage = true;
            }
        });
        add(inputTextField, BorderLayout.SOUTH); 
    }
    
    public void run()
    {
        GUI frame = new GUI();
        frame.setSize(400,300);
        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e)
            {
                try
                {
                    ChatClient.socket.close();
                    System.exit(0);
                }
                catch(Exception excp) {};
            }
        });
    }
    
    public void actionPerformed(ActionEvent event)
    {
        
    }
}