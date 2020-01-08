package com.example.treking_gps;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private static int MAX_MESSAGE_LENGTH = 100;

    private RecyclerView mMessageRecycler;
    private EditText mEditTextMessage;
    private Button mSendButton;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference("messages");
    private ArrayList<String> messages = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSendButton = findViewById(R.id.send_message_b);
        mEditTextMessage = findViewById(R.id.message_input);
        mMessageRecycler = findViewById(R.id.message_recycler);
        mMessageRecycler.setLayoutManager(new LinearLayoutManager(this));


        final DataAdapter dataAdapter = new DataAdapter(this, messages);


        mMessageRecycler.setAdapter(dataAdapter);


        mSendButton.setOnClickListener(v -> {
            String mag = mEditTextMessage.getText().toString();

            if (mag.equals("")) {
                Toast.makeText(getApplicationContext(), "Введите сообщение!", Toast.LENGTH_LONG).show();
                return;
            }
            if (mag.length() == MAX_MESSAGE_LENGTH) {
                Toast.makeText(getApplicationContext(), "Слишком длиное сообщение", Toast.LENGTH_LONG).show();
                return;
            }


            myRef.push().setValue(mag);
            mEditTextMessage.setText("");
        });


        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String msg = dataSnapshot.getValue(String.class);
                messages.add(msg);
                dataAdapter.notifyDataSetChanged();
                mMessageRecycler.smoothScrollToPosition(messages.size());
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

}
