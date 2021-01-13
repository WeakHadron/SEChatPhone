package ir.alizadeh.sechat.Utils;

import android.os.AsyncTask;
import android.os.Handler;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

import ir.alizadeh.sechat.Activities.ChatRoomActivity;
import ir.alizadeh.sechat.Adapters.ChatListAdapter;
import ir.alizadeh.sechat.Models.MessageModel;

public class SendMessageJob implements Runnable{

    String text;
    ChatListAdapter adapter;
    List<MessageModel> list;
    Handler handler;
    public SendMessageJob(String text,ChatListAdapter adapter,List<MessageModel> list, Handler handler){
        this.text = text;
        this.adapter = adapter;
        this.list = list;
        this.handler = handler;
    }
    @Override
    public void run() {
        String dadada = text;
        handler.post(new Runnable() {
            @Override
            public void run() {
                String d = text;

            }
        });

    }
}