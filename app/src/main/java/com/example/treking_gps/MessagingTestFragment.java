package com.example.treking_gps;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

public class MessagingTestFragment extends FragmentButterKnife {

    private static int MAX_MESSAGE_LENGTH = 100;

    @BindView(R.id.message_recycler)
    protected RecyclerView messagesRecyclerView;
    @BindView(R.id.message_input)
    protected EditText messageInputField;
    @BindView(R.id.send_message_b)
    protected Button sendButton;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference("messages");
    private ArrayList<String> messages = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.screen_messaging_test;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final DataAdapter dataAdapter = new DataAdapter(getContext(), messages);

        messagesRecyclerView.setAdapter(dataAdapter);
        messagesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String msg = dataSnapshot.getValue(String.class);
                messages.add(msg);
                dataAdapter.notifyDataSetChanged();
                messagesRecyclerView.smoothScrollToPosition(messages.size());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @OnClick(R.id.send_message_b)
    protected void sendMessage() {
        String message = messageInputField.getText().toString();

        if (message.equals("")) {

            Toast.makeText(getContext(), "Введите сообщение!", Toast.LENGTH_LONG).show();
            return;
        }
        if (message.length() >= MAX_MESSAGE_LENGTH) {
            Toast.makeText(getContext(), "Слишком длиное сообщение", Toast.LENGTH_LONG).show();
            return;
        }


        myRef.push().setValue(message);
        messageInputField.setText("");
    }
}
