package ir.alizadeh.sechat.Activities;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import ir.alizadeh.sechat.Adapters.ChatListAdapter;
import ir.alizadeh.sechat.Utils.SocketFactory;
import ir.alizadeh.sechat.Views.CustomDialog;
import ir.alizadeh.sechat.Models.MessageModel;
import ir.alizadeh.sechat.R;

public class ChatRoomActivity extends AppCompatActivity {
    List<MessageModel> messages;
    RecyclerView recyclerView;
    EditText textView;

    FloatingActionButton fab;
    boolean isResized;
    FloatingActionButton CloseChatBtn;
    int frameInitWidth;
    Socket socket;
    BufferedReader input;
    OutputStream outputStream;
    PrintWriter writer;
    String s;
    String chatText;
    ChatListAdapter adapter;
    SendJob job;
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
        job = new SendJob();
        final Handler handler = new Handler();
        dialog.show();
        Runnable ReadSocket = new Runnable() {
            @Override
            public void run() {

                try {
                    SocketFactory.interr = false;
                    socket = SocketFactory.getSocket(addr,6985);
                     input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                     handler.postDelayed(new Runnable() {
                         @Override
                         public void run() {
                             dialog.dismiss();
                         }
                     },800);
                    while(!socket.isClosed() && !SocketFactory.interr) {

                        s = input.readLine();
                        if (s != null && !s.equals("exit")) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {



                                    if (!SocketFactory.interr && !s.equals("")) {
                                        messages.add(new MessageModel(s, false));
                                        adapter.notifyDataSetChanged();
                                    }

                                }

                            });
                        }else if(s.equals("exit")){
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        SocketFactory.destroySocket();
                                        SocketFactory.interr = true;
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
                    SocketFactory.interr = true;

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
        //dialog.show();

        isResized = false;


        CloseChatBtn = findViewById(R.id.close_chat_button);
        CloseChatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new SendJob().execute("exit");



            }
        });
        fab = findViewById(R.id.send_message_button);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        messages = new ArrayList<>();
        adapter = new ChatListAdapter(messages,this);
        recyclerView = findViewById(R.id.chat_list);
        recyclerView.setAdapter(adapter);
        textView = findViewById(R.id.chat_edit_text);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter.notifyDataSetChanged();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatText = textView.getText().toString();

                new SendJob().execute(chatText);
                if(chatText.trim().equals("exit")){
                    finish();
                }

            }
        });

        textView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!isResized && !textView.getText().equals("")) {
                    fadeAndShowButton(fab,0,1);
                    isResized = true;
                }
                else if(textView.getText().toString().equals("")){
                    fadeAndShowButton(fab,1,0);
                    isResized = false;
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    public void fadeAndShowButton(final FloatingActionButton button, int from, int to){
        Animation fadeout = new AlphaAnimation(from,to);
        fadeout.setInterpolator(new AccelerateInterpolator());
        fadeout.setDuration(150);
        fadeout.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @SuppressLint("RestrictedApi")
            @Override
            public void onAnimationEnd(Animation animation) {
                if(isResized) {
                    button.setVisibility(View.VISIBLE);
                }else{
                    button.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        button.startAnimation(fadeout);

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            input.close();
            writer.close();
            outputStream.close();
            SocketFactory.destroySocket();
            SocketFactory.interr = true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("SOCKET_CLOSURE_ERROR",e.getMessage());
        }
    }
   private class SendJob extends AsyncTask<String,Void,Boolean>{
       @Override
       protected void onPreExecute() {
           super.onPreExecute();
           fab.setClickable(false);
       }

       @Override
        protected Boolean doInBackground(String... strings) {
            try{

                socket = SocketFactory.getSocket(addr,6985);



                outputStream = socket.getOutputStream();
                writer = new PrintWriter(outputStream);
                writer.print(strings[0]);
                writer.flush();

                if(strings[0].equals("exit")){
                    return false;
                }
                return true;



            }catch (Exception e){
                return false;
            }

        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            textView.setText("");
            fab.setClickable(true);
            if(aBoolean){


                messages.add(new MessageModel(chatText,true));
                adapter.notifyDataSetChanged();



            }else{
                finish();
            }
        }
    }
}