package com.example.hp.sstgp;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class Chat_Activity extends AppCompatActivity {
    private String mchatUser;
    private DatabaseReference rootref;
    private FirebaseAuth mAuth;
    private String mCurrentUserID;
    private Button msendButton;
    private EditText mchatmessageview;
    private RecyclerView mMessagesList;
    private final List<Messages> MessageList = new ArrayList<>();
    private LinearLayoutManager mLinearlayout;
    private MessageAdapter mAdapter;




    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_);
        mchatUser= getIntent().getStringExtra("User ID");

        mAuth = FirebaseAuth.getInstance();
        mCurrentUserID = mAuth.getCurrentUser().getUid().toString();

        getSupportActionBar().setTitle(mchatUser);

        rootref = FirebaseDatabase.getInstance().getReference();
        String UserName = getIntent().getStringExtra("username");
        getSupportActionBar().setTitle(UserName);

        mAdapter = new MessageAdapter(MessageList);

        mMessagesList = findViewById(R.id.messages_list);
        mLinearlayout=new LinearLayoutManager(this);

        mMessagesList.setHasFixedSize(true);
        mMessagesList.setLayoutManager(mLinearlayout);

        mMessagesList.setAdapter(mAdapter);
        loadMessages();
//--------

        mchatmessageview = findViewById(R.id.editText9);
        msendButton = findViewById(R.id.sendButton);
        rootref.child("chat").child(mCurrentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild(mchatUser)){

                    Map chatAddMap = new HashMap();
                    chatAddMap.put("seen", false);

                    Map chatUsermap = new HashMap();
                    chatUsermap.put("Chat/"+mCurrentUserID+"/"+mchatUser,chatAddMap);
                    chatUsermap.put("Chat/"+mchatUser+"/"+mCurrentUserID,chatAddMap);

                    rootref.updateChildren(chatUsermap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if(databaseError != null){
                                Log.d("Chat_LOG",databaseError.getMessage().toString());
                            }

                        }
                    });

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        msendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });




    }
    //----
    public void loadMessages(){
        rootref.child("messages").child(mCurrentUserID).child(mchatUser).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Messages message= dataSnapshot.getValue(Messages.class);

                MessageList.add(message);
                mAdapter.notifyDataSetChanged();

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
    public  void sendMessage(){
        String message = mchatmessageview.getText().toString();
        if(!TextUtils.isEmpty(message)){
            String currentUserRef = "messages/"+ mCurrentUserID +"/"+mchatUser ;
            String chat_user_ref ="messages/"+ mchatUser +"/"+mCurrentUserID ;

            DatabaseReference user_message_push =rootref.child("messages").child(mCurrentUserID).child(mchatUser).push();
            String push_ID = user_message_push.getKey();
            Map messageMap = new HashMap();
            messageMap.put("message", message );
            messageMap.put("seen", false);
            messageMap.put("type","text");
            messageMap.put("from",mCurrentUserID);

            Map muserMap = new HashMap();
            muserMap.put(currentUserRef+"/" + push_ID, messageMap);
            muserMap.put(chat_user_ref+"/" + push_ID, messageMap);
            mchatmessageview.setText("");

            rootref.updateChildren(muserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    if(databaseError != null){
                        Log.d("Chat_LOG",databaseError.getMessage().toString());
                    }
                }
            });

        }
    }
}
