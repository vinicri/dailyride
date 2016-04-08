package com.dingoapp.dingo.chat;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dingoapp.dingo.R;
import com.dingoapp.dingo.api.model.User;
import com.dingoapp.dingo.chat.api.FirebaseApi;
import com.dingoapp.dingo.util.CurrentUser;

import java.util.Map;

/**
 * Created by guestguest on 10/03/16.
 */
public class ChatAdapter extends FirebaseAdapter<ChatMessage> {

    private final int TYPE_SYSTEM = 1;
    private final int TYPE_USER = 2;
    private final int TYPE_THIS_USER = 3;

    private final Context mContext;
    //private final FirebaseSyncer<ChatUser> mUsersSyncer;
    private Map<Long, User> mUsersMap;

    public ChatAdapter(Context context, long offerId, Map<Long, User> users){
        super(context, FirebaseApi.getChatMessagesUrl(offerId), ChatMessage.class, null);

        /*FirebaseSyncer.Listener mUsersSyncListener = new FirebaseSyncer.Listener() {
            @Override
            public void notifyDataSetChanged() {

            }
        };

        mUsersSyncer = new FirebaseSyncer<ChatUser>(context,
                FirebaseApi.getChatUsersUrl(offerId),
                ChatUser.class, null,
                mUsersSyncListener);*/

        mContext = context;
        mUsersMap = users;
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
        ChatMessage message = getModels().get(position);
        User user;
        switch (holder.getItemViewType()){
            case TYPE_USER:
                UserMessageViewHolder userMessageViewHolder = (UserMessageViewHolder)holder;
                //safeguard
                user = mUsersMap.get(message.getUserId());
                if(user != null){
                    userMessageViewHolder.mUsername.setText(user.getFirstName());
                }
                else{
                    //todo
                }
                userMessageViewHolder.mContent.setText(message.getMessage());
                break;
            case TYPE_THIS_USER:
                ThisUserMessageViewHolder thisUserMessageViewHolder = (ThisUserMessageViewHolder)holder;
                thisUserMessageViewHolder.mContent.setText(message.getMessage());
                break;
            default:
                SystemMessageViewHolder systemMessageViewHolder = (SystemMessageViewHolder)holder;
                if(message.getSubtype() != null && message.getSubtype().equals(ChatMessage.SUBTYPE_USER_ADDED)){
                    user = mUsersMap.get(message.getUserId());
                    if(user != null){
                        mContext.getResources().getString(R.string.chat_user_added, user.getFullName());
                    }
                    else{
                        //todo not expected
                    }
                }
                //systemMessageViewHolder.mContent.setText(message.getMessage());
                break;
        }

    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage message = getModels().get(position);
        if(message.getType() == ChatMessage.TYPE_SYSTEM){
            return TYPE_SYSTEM;
        }
        else{
            if(message.getUserId().equals(CurrentUser.getUser().getId())){
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
