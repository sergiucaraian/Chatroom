package cc.chatclient;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
    private Button joinButton;
    private EditText nameField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        joinButton = (Button) findViewById(R.id.btnJoin);
        nameField = (EditText) findViewById(R.id.name);
        getActionBar().hide();

        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nameField.getText().toString().trim().length() > 0) {
                    String name = nameField.getText().toString().trim();
                    Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                    intent.putExtra("name", name);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Please enter your name", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}



