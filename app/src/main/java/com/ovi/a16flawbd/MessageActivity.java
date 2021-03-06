package com.ovi.a16flawbd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ovi.a16flawbd.Adapter.MessageAdapter;
import com.ovi.a16flawbd.Model.Chat;
import com.ovi.a16flawbd.Model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {
    CircleImageView profileImage;
    TextView username;

    FirebaseUser fuser;
    DatabaseReference reference;

    ImageButton btn_send;
    EditText txt_send;

    MessageAdapter messageAdapter;
    List<Chat> mChat;

    RecyclerView recyclerView;

    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        intent = getIntent();
        final String userId = intent.getStringExtra("userId");

        profileImage = findViewById(R.id.profileImage);
        username = findViewById(R.id.username);
        btn_send = findViewById(R.id.btn_send);
        txt_send = findViewById(R.id.send_text);

        recyclerView = findViewById(R.id.recyclear_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = txt_send.getText().toString();
                if (msg.equals("")){
                    Toast.makeText(MessageActivity.this, "you can't send empty message", Toast.LENGTH_SHORT).show();
                }else {
                    sendMessage(fuser.getUid(),userId, msg);
                }
                txt_send.setText("");
            }
        });


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });




        reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);// child comes from useradapter

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                username.setText(user.getUsername());
                if (user.getImageUrl().equals("Default")){
                    profileImage.setImageResource(R.mipmap.ic_launcher_round);
                    Glide.with(MessageActivity.this).load("http://goo.gl/gEgYUd").into(profileImage);
                }else {
                    Glide.with(MessageActivity.this).load(user.getImageUrl()).into(profileImage);
                }
                // calling readMessage
                readMessage(fuser.getUid(), userId, user.getImageUrl());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    // section for sending message. theme is to collect data from user, match sender id, reciever id and deliver to that id
    public void sendMessage(String sender, String receiver, String message){
        // getting reference of root directory
        DatabaseReference databaseReferencere = FirebaseDatabase.getInstance().getReference();
        // creating hashmap for sending all data together
        HashMap<String, Object>  hashMap = new HashMap<>();
        //putting datas on hashmap
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        // putting data to database by creating a child in the root
        databaseReferencere.child("Chats").push().setValue(hashMap);
    }

    // displaying chats
    public void readMessage(String myid, String userid, String imageUrl){
        mChat = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    mChat.clear();
                    for (DataSnapshot snapshot1: snapshot.getChildren()){
                        Chat chat = snapshot1.getValue(Chat.class);
                        assert chat != null;
                        if (chat.getReceiver().equals(myid) && chat.getSender().equals(userid) ||
                                chat.getReceiver().equals(userid) && chat.getSender().equals(myid)){
                            mChat.add(chat);
                        }
                        messageAdapter = new MessageAdapter(MessageActivity.this, mChat, imageUrl);
                        recyclerView.setAdapter(messageAdapter);
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}