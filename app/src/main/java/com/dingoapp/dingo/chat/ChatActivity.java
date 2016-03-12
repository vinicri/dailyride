package com.dingoapp.dingo.chat;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.dingoapp.dingo.BaseActivity;
import com.dingoapp.dingo.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by guestguest on 09/03/16.
 */
public class ChatActivity extends BaseActivity {

    @Bind(R.id.messages_list)RecyclerView mRecyclerView;
    @Bind(R.id.message_edit) EditText mMessageEdit;
    @Bind(R.id.send) ImageView mSendButton;
    private ChatAdapter mAdapter;

    List<ChatMessage> mMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        getSupportActionBar().setTitle(R.string.activity_chat);
        ButterKnife.bind(this);

        ChatMessage message0 = new ChatMessage();
        message0.setMessage("Pedro entrou nesta carona");
        message0.setType(ChatMessage.TYPE_SYSTEM);

        ChatMessage message1 = new ChatMessage();
        message1.setUserId(1l);
        message1.setMessage("Oi Vinícius! Estou na calçada, de camisa verde listrada");
        message1.setType(ChatMessage.TYPE_USER);

        ChatMessage message2 = new ChatMessage();
        message2.setUserId(0l);
        message2.setMessage("Ok Pedro. Obrigado.");
        message2.setType(ChatMessage.TYPE_USER);

        mMessages = new ArrayList<>();
        mMessages.add(message0);
        mMessages.add(message1);
        //mMessages.add(message2);

        mAdapter = new ChatAdapter(this, mMessages);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mSendButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mMessageEdit.getText().length() > 0) {
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
        );
    }
}
