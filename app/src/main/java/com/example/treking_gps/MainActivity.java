package com.example.treking_gps;

import android.Manifest;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import pl.charmas.android.reactivelocation2.ReactiveLocationProvider;


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


        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
            }
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

    @Override
    protected void onResume() {
        super.onResume();
        requestPermissions();
    }

    private void requestPermissions() {
        List<Permission> permissions = new ArrayList<>();

        new RxPermissions(this)
                .requestEach(
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                )
                .subscribe(new DisposableObserver<Permission>() {
                    @Override
                    public void onNext(Permission permission) {
                        permissions.add(permission);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        checkPermissions(permissions);
                    }
                });
    }

    private void checkPermissions(List<Permission> permissions) {
        for (Permission item: permissions) {
            Log.d(item.name, "granted: "+item.granted);
        }
        startRequestLocation();
    }

    private void startRequestLocation() {
        ReactiveLocationProvider locationProvider =  new  ReactiveLocationProvider (context);
        locationProvider.getLastKnownLocation()
                .subscribeOn(Schedulers.io())               // use I/O thread to query for addresses
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer <Location>() {
                    @Override
                    public void call(Location location) {
                        doSthImportantWithObtainedLocation(location);
                    }
                });
    }
}
