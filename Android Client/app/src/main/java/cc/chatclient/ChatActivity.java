package cc.chatclient;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.json.JSONException;
import org.json.JSONObject;


public class ChatActivity extends Activity {
    private Button sendButton;
    private EditText inputMessage;

    private String username = null;

    private MessagesListAdapter adapter;
    private List<Message> listMessages;
    private ListView listViewMessages;

    Thread networkThread;
    Handler sendHandler = null;
    Handler receiveHandler = null;

    boolean isNewOutputMessage = false;
    String newOutputMessage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        sendButton = (Button) findViewById(R.id.btnSend);
        inputMessage = (EditText) findViewById(R.id.inputMsg);
        listViewMessages = (ListView) findViewById(R.id.list_view_messages);

        Intent i = getIntent();
        username = i.getStringExtra("name");

        listMessages = new ArrayList<Message>();
        adapter = new MessagesListAdapter(this,listMessages);
        listViewMessages.setAdapter(adapter);

        sendHandler = new Handler();
        receiveHandler = new Handler();

        networkThread = new Thread(new NetworkThread());
        networkThread.start();

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newOutputMessage = inputMessage.getText().toString();
                isNewOutputMessage = true;
                inputMessage.setText("");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        networkThread.interrupt();
    }

    private void parseMessage(final String msg) {
        try {
            JSONObject jObj = new JSONObject(msg);
            String flag = jObj.getString("flag");

            if(flag.equals("new")) {
                String name = jObj.getString("name");
                showToast(name + " connected");
            }

            else if(flag.equals("message")) {
                String name = jObj.getString("name");
                String mg = jObj.getString("message");
                boolean isFrom = true;
                if(!name.equals(username))
                    isFrom = false;
                Message m = new Message(name,mg,isFrom);
                appendMessage(m);
            }

            else if(flag.equals("exit")) {
                String name = jObj.getString("name");
                showToast(name + " disconnected");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showToast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
            }
        });
    }

    private void appendMessage(final Message m) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listMessages.add(m);
                adapter.notifyDataSetChanged();
            }
        });
    }

    class NetworkThread implements Runnable {
        private InetAddress host;
        private final int PORT = 1234;
        Socket socket = null;

        public void run() {
            try {
                //host = InetAddress.getLocalHost();
                socket = new Socket("192.168.0.100", PORT);

                Scanner inputStream = new Scanner(socket.getInputStream());
                PrintWriter outputStream = new PrintWriter(socket.getOutputStream(),true);
                outputStream.println(JSONUtils.getAuthentication(username));

                Thread receiveThread = new Thread(new ReceiverThread(socket, inputStream));
                receiveThread.start();

                while (!Thread.currentThread().isInterrupted()) {
                    if(isNewOutputMessage) {
                        String json = JSONUtils.getMessage(username, newOutputMessage);
                        isNewOutputMessage = false;
                        outputStream.println(json);
                    }
                }

                receiveThread.interrupt();

            } catch (Exception e) {
                return;
            }
        }
    }


    class ReceiverThread implements Runnable {
        Socket socket;
        Scanner inputStream;
        String message;

        ReceiverThread(Socket socket, Scanner inputStream) {
            this.socket = socket;
            this.inputStream = inputStream;
        }

        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                parseMessage(inputStream.nextLine());
            }
        }
    }
}
