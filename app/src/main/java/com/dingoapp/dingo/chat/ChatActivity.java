package com.dingoapp.dingo.chat;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.dingoapp.dingo.BaseActivity;
import com.dingoapp.dingo.R;
import com.dingoapp.dingo.api.model.User;
import com.dingoapp.dingo.chat.api.FirebaseApi;
import com.dingoapp.dingo.util.CurrentUser;
import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by guestguest on 09/03/16.
 */
public class ChatActivity extends BaseActivity {

    public static final String EXTRA_OFFER_ID = "extra_offer_id";
    public static final String EXTRA_USERS = "extra_users";

    @Bind(R.id.messages_list)RecyclerView mRecyclerView;
    @Bind(R.id.message_edit) EditText mMessageEdit;
    @Bind(R.id.send) ImageView mSendButton;
    private ChatAdapter mAdapter;

    List<ChatMessage> mMessages;
    private long mOfferId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        getSupportActionBar().setTitle(R.string.activity_chat);
        ButterKnife.bind(this);

        mOfferId = getIntent().getIntExtra(EXTRA_OFFER_ID, -1);

        if(mOfferId == -1){
            //todo not expected error
        }

        List<User> users = (ArrayList<User>)getIntent().getSerializableExtra(EXTRA_USERS);

        Map<Long, User> usersMap = new HashMap<>();
        for(User user: users){
            usersMap.put(user.getId(), user);
        }

        mAdapter = new ChatAdapter(this, mOfferId, usersMap);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);

        /*mSendButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mMessageEdit.getText().length() > 0) {
                            ChatMessage message = new ChatMessage();
                            message.setType(ChatMessage.TYPE_USER);
                            message.setUserId(0l);
                            message.setMessage(mMessageEdit.getText().toString());
                            mMessages.add(message);
                            mAdapter.notifyItemInserted(mMessages.indexOf(message));
                            mMessageEdit.setText("");
                        }

                    }
                }
        );*/

        FirebaseApi.getAuthenticatedSession(this, FirebaseApi.getChatMessagesUrl(mOfferId),
                new FirebaseApi.AuthCallback() {
                    @Override
                    public void onAuthenticated(final Firebase ref) {
                        mSendButton.setOnClickListener(
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (mMessageEdit.getText().length() > 0) {
                                            ChatMessage message = new ChatMessage();
                                            message.setType(ChatMessage.TYPE_USER);
                                            message.setUserId(CurrentUser.getUser().getId());
                                            message.setMessage(mMessageEdit.getText().toString());
                                            message.setTimestamp(new Date());
                                            ref.push().setValue(message);
                                            mMessageEdit.setText("");
                                        }
                                    }
                                }
                        );
                    }

                    @Override
                    public void onAuthenticationError() {

                    }
                },
                true);
    }


}
