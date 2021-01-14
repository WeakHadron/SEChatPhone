package ir.alizadeh.sechat.adapters;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ir.alizadeh.sechat.models.MessageModel;
import ir.alizadeh.sechat.R;


public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatViewHolder> {
    volatile private List<MessageModel> messages;
    Context context;
    public ChatListAdapter(List<MessageModel> msgArray, Context context){
        messages = msgArray;
        this.context = context;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.chat_item_layout,parent,false);
        ChatViewHolder viewHolder = new ChatViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        holder.textView.setText(messages.get(position).getMsg());
        if(!messages.get(position).isSelf()) {
            //holder.textView.setBackground(ContextCompat.getDrawable(context, R.drawable.chatroom_toolbar_and_edittext_background));
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.LEFT;
            holder.textView.setLayoutParams(params);
        }
        holder.textView.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.chat_text);


        }
    }
}
