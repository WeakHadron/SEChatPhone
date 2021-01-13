package ir.alizadeh.sechat.Activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import android.widget.Button;
import android.widget.EditText;

import ir.alizadeh.sechat.R;

public class MainActivity extends AppCompatActivity {
    Button connectToServer;
    EditText roomAddr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        connectToServer = findViewById(R.id.connect_to_server_btn);
        roomAddr = findViewById(R.id.room_join_addr_edt);

        connectToServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String addr = roomAddr.getText().toString();
                Intent intent = new Intent(MainActivity.this, ChatRoomActivity.class);
                intent.putExtra("addr",addr);
                startActivity(intent);
            }
        });




    }


}