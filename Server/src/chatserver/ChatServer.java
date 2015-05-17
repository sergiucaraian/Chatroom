package chatserver;

import java.util.*;
import java.net.*;
import java.io.*;
import java.util.concurrent.*;
import org.json.JSONException;
import org.json.JSONObject;

public class ChatServer {
    private static ServerSocket servSocket;
    private static final int PORT = 1234;
    static ArrayList <Connection> connections = new ArrayList<>();
    
    public static void main(String[] args) throws IOException {  
        
        try {
            servSocket = new ServerSocket(PORT);
        } catch (IOException ioEx) {
            System.out.println("Unable to set up port!");
        }
        System.out.println("Server initialised");
        
        MessageHandler messageHandler = new MessageHandler();
        Thread messageThread = new Thread(messageHandler);
        messageThread.start();
        
        while(true) {
            Socket clientSocket = servSocket.accept();
            Connection currentConnection = new Connection(clientSocket);
            currentConnection.start();
            connections.add(currentConnection);
        }
    }
}

class MessageHandler implements Runnable {
    public void run() {
        while(true) {
            if(!Database.messageQueue.isEmpty()) {
                String json = Database.messageQueue.remove();
                
                for(Iterator<Connection> iter = ChatServer.connections.iterator(); iter.hasNext();) {
                    Connection c = iter.next();
                    c.outputStream.println(json);
                }
            }
        }
    }
}

class Database {
    static Queue<String> messageQueue =  new ConcurrentLinkedQueue<>();
}

class Connection extends Thread {
    Socket socket;
    String username;
    Scanner inputStream;
    PrintWriter outputStream;
    
    Connection(Socket clientSocket) {
        this.socket = clientSocket;
    }
    
    @Override
    public void run() {
        try {
            inputStream = new Scanner(socket.getInputStream());
            outputStream = new PrintWriter(socket.getOutputStream(),true);
            
            String jMessage = inputStream.nextLine();
            JSONObject jObj = new JSONObject(jMessage);
            username = jObj.getString("name");
            
            System.out.println(username + " connected");
            String confirmConnection = JSONUtils.getConfirmation();
            outputStream.println(confirmConnection);
            Database.messageQueue.add(JSONUtils.getNewClient(username));
            
            boolean exitSession = false;
            do {
                jMessage = inputStream.nextLine();
                jObj = new JSONObject(JSONUtils.getMessage(username, jMessage));
                
                if(jObj.getString("flag").equals("exit")) {
                    exitSession = true;
                }
                
                else if(jObj.getString("flag").equals("message")) {
                    Database.messageQueue.add(jObj.getString("message"));
                    System.out.println("Message recieved from " + username);
                }
            } while(!exitSession);
        } catch(Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                close();
                socket.close();
            }
            catch(IOException ioEx) {
                System.out.println("Unable to disconnect user " + username);
            }
        }
    }
    
    void close() {
        Database.messageQueue.add(JSONUtils.getExitClient(username));
        System.out.println(username + " disconnected");
    }
}
