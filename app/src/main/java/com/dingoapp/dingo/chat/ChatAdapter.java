package com.dingoapp.dingo.chat;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dingoapp.dingo.R;

import java.util.HashMap;
import java.util.List;

/**
 * Created by guestguest on 10/03/16.
 */
public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int TYPE_SYSTEM = 1;
    private final int TYPE_USER = 2;
    private final int TYPE_THIS_USER = 3;

    private final Context mContext;
    private final List<ChatMessage> mMessages;
    private HashMap<Long, ChatUser> mUsersMap;

    public ChatAdapter(Context context, List<ChatMessage> messages){
        mContext = context;
        mMessages = messages;
        mUsersMap = new HashMap<>();

        ChatUser user1 = new ChatUser();
        user1.setId(1l);
        user1.setFirstName("Greice");

        mUsersMap.put(user1.getId(), user1);

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType){
            case TYPE_THIS_USER:
                View v1 = inflater.inflate(R.layout.item_chat_message_this_user, parent, false);
                viewHolder = new ThisUserMessageViewHolder(v1);
                break;
            case TYPE_USER:
                View v2 = inflater.inflate(R.layout.item_chat_message_user, parent, false);
                viewHolder = new UserMessageViewHolder(v2);
                break;
            default:
                View v3 = inflater.inflate(R.layout.item_chat_message_system, parent, false);
                viewHolder = new SystemMessageViewHolder(v3);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = mMessages.get(position);
        switch (holder.getItemViewType()){
            case TYPE_USER:
                UserMessageViewHolder userMessageViewHolder = (UserMessageViewHolder)holder;
                userMessageViewHolder.mUsername.setText(mUsersMap.get(message.getUserId()).getFirstName());
                userMessageViewHolder.mContent.setText(message.getMessage());
                break;
            case TYPE_THIS_USER:
                ThisUserMessageViewHolder thisUserMessageViewHolder = (ThisUserMessageViewHolder)holder;
                thisUserMessageViewHolder.mContent.setText(message.getMessage());
                break;
            default:
                SystemMessageViewHolder systemMessageViewHolder = (SystemMessageViewHolder)holder;
                systemMessageViewHolder.mContent.setText(message.getMessage());
                break;
        }

    }

    @Override
    public int getItemCount() {
        if(mMessages != null){
            return mMessages.size();
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage message = mMessages.get(position);
        if(message.getType() == ChatMessage.TYPE_SYSTEM){
            return TYPE_SYSTEM;
        }
        else{
            if(message.getUserId().equals(0l)){//CurrentUser.getUser().getId())){
                return TYPE_THIS_USER;
            }
            else{
                return TYPE_USER;
            }
        }
    }

    public static class SystemMessageViewHolder extends RecyclerView.ViewHolder{
        TextView mContent;
        public SystemMessageViewHolder(View itemView) {
            super(itemView);
            mContent = (TextView)itemView.findViewById(R.id.content);
        }
    }

    public static class ThisUserMessageViewHolder extends RecyclerView.ViewHolder{
        TextView mContent;

        public ThisUserMessageViewHolder(View itemView) {
            super(itemView);
            mContent = (TextView)itemView.findViewById(R.id.content);
        }
    }

    public static class UserMessageViewHolder extends RecyclerView.ViewHolder{
        TextView mUsername;
        TextView mContent;
        public UserMessageViewHolder(View itemView) {
            super(itemView);
            mContent = (TextView)itemView.findViewById(R.id.content);
            mUsername = (TextView)itemView.findViewById(R.id.user_name);
        }
    }
}
