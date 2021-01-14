package ir.alizadeh.sechat.activities;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import ir.alizadeh.sechat.adapters.ChatListAdapter;
import ir.alizadeh.sechat.utils.SendMessageJob;
import ir.alizadeh.sechat.utils.SocketFactory;
import ir.alizadeh.sechat.views.CustomDialog;
import ir.alizadeh.sechat.models.MessageModel;
import ir.alizadeh.sechat.R;
import ir.alizadeh.sechat.animations.AnimationUtils;

public class ChatRoomActivity extends AppCompatActivity {

    List<MessageModel> messages;
    RecyclerView recyclerView;
    EditText chatTextInput;
    FloatingActionButton sendMessageBtn;
    boolean isResized;
    FloatingActionButton closeChatBtn;
    Socket socket;
    BufferedReader input;
    OutputStream outputStream;
    PrintWriter writer;
    String readText;
    String chatText;
    ChatListAdapter adapter;
    String addr;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        Intent intent = getIntent();
        addr = intent.getStringExtra("addr");

        final CustomDialog dialog = new CustomDialog(this);
        final Handler handler = new Handler();
        dialog.show();
        Runnable ReadSocket = new Runnable() {
            @Override
            public void run() {

                try {
                    SocketFactory.interrupted = false;
                    socket = SocketFactory.getSocket(addr,6985);
                     input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                     handler.postDelayed(new Runnable() {
                         @Override
                         public void run() {
                             dialog.dismiss();
                         }
                     },800);
                    while(!socket.isClosed() && !SocketFactory.interrupted) {

                        readText = input.readLine();
                        if (readText != null && !readText.equals("exit")) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {



                                    if (!SocketFactory.interrupted && !readText.equals("")) {
                                        messages.add(new MessageModel(readText, false));
                                        adapter.notifyDataSetChanged();
                                    }

                                }

                            });
                        }else if(readText.equals("exit")){
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        SocketFactory.destroySocket();
                                        SocketFactory.interrupted = true;
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    finish();
                                }
                            });
                        }
                        else{
                            SystemClock.sleep(200);
                        }
                    }

                    input.close();
                    SocketFactory.destroySocket();
                    SocketFactory.interrupted = true;

                } catch (Exception e) {
                   e.printStackTrace();
                   handler.postDelayed(new Runnable() {
                       @Override
                       public void run() {
                           dialog.dismiss();
                           finish();
                       }
                   },800);

               }

            }
        };


        final Thread ReadSocketThread = new Thread(ReadSocket);
        ReadSocketThread.start();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        isResized = false;
        closeChatBtn = findViewById(R.id.close_chat_button);
        closeChatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                provideSender().execute("exit");



            }
        });

        sendMessageBtn = findViewById(R.id.send_message_button);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        messages = new ArrayList<>();
        adapter = new ChatListAdapter(messages,this);
        recyclerView = findViewById(R.id.chat_list);
        recyclerView.setAdapter(adapter);
        chatTextInput = findViewById(R.id.chat_edit_text);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter.notifyDataSetChanged();


        sendMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatText = chatTextInput.getText().toString();

                provideSender().execute("exit");
                if(chatText.trim().equals("exit")){
                    finish();
                }

            }
        });


        chatTextInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!isResized && !chatTextInput.getText().equals("")) {
                    AnimationUtils.fadeAndShowButton(sendMessageBtn,0,1,isResized);
                    isResized = true;
                }
                else if(chatTextInput.getText().toString().equals("")){
                    AnimationUtils.fadeAndShowButton(sendMessageBtn,1,0,isResized);
                    isResized = false;
                }

            }
            @Override
            public void afterTextChanged(Editable s) { }
        });

    }


    SendMessageJob provideSender(){
        return new SendMessageJob(socket,
                addr,outputStream
                ,writer,chatTextInput
                ,sendMessageBtn,this
                ,chatText,messages
                ,adapter);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            input.close();
            writer.close();
            outputStream.close();
            SocketFactory.destroySocket();
            SocketFactory.interrupted = true;
        } catch (Exception e) { }


    }

}