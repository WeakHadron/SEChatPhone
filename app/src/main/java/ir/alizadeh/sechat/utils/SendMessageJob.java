package ir.alizadeh.sechat.utils;

import android.os.AsyncTask;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

import ir.alizadeh.sechat.activities.ChatRoomActivity;
import ir.alizadeh.sechat.adapters.ChatListAdapter;
import ir.alizadeh.sechat.models.MessageModel;

public class SendMessageJob extends AsyncTask<String,Void,Boolean>{

    Socket socket;
    String addr;
    OutputStream outputStream;
    PrintWriter writer;
    TextView textView;
    FloatingActionButton sendMessageBtn;
    ChatRoomActivity activity;
    String chatText;
    List<MessageModel> messages;
    ChatListAdapter adapter;



    public SendMessageJob(Socket socket
            , String addr
            , OutputStream outputStream
            , PrintWriter writer
            , TextView textView
            , FloatingActionButton sendMessageBtn
            , ChatRoomActivity activity
            , String chatText
            , List<MessageModel> messages
            , ChatListAdapter adapter){

        this.socket =socket;
        this.addr = addr;
        this.outputStream = outputStream;
        this.writer = writer;
        this.textView = textView;
        this.sendMessageBtn = sendMessageBtn;
        this.activity = activity;
        this.chatText = chatText;
        this.messages = messages;
        this.adapter = adapter;


    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        sendMessageBtn.setClickable(false);
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        try{

            socket = SocketFactory.getSocket(addr,6985);



            outputStream = socket.getOutputStream();
            writer = new PrintWriter(outputStream);
            writer.print(strings[0]);
            writer.flush();

            return !strings[0].equals("exit");


        }catch (Exception e){
            return false;
        }

    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        textView.setText("");
        sendMessageBtn.setClickable(true);
        if(aBoolean){


            messages.add(new MessageModel(chatText,true));
            adapter.notifyDataSetChanged();



        }else{
            activity.finish();
        }
    }
}