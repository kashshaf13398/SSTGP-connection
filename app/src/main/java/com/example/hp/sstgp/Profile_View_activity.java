package com.example.hp.sstgp;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

public class Profile_View_activity extends AppCompatActivity implements View.OnClickListener {
    private TextView mDisplayName;
    private ImageView imageView;
    private Button sendreq_button,decline_Button;
    private DatabaseReference mDatabaseRef,mFriendreqDatabase,FriendDatabase;
    private ProgressDialog mProgressDialogue;
    private DatabaseReference mNotificatiodatabase;
    private String mState;
    private FirebaseUser mCurrentUser;
    String UserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile__view_activity);
        UserId= getIntent().getStringExtra("User ID" );
        mDisplayName = findViewById(R.id.profile_display_name);
        mDisplayName.setText(UserId);
        imageView = findViewById(R.id.profile_view_image);
        sendreq_button = findViewById(R.id.friend_req_button);
        sendreq_button.setOnClickListener(this);
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child(UserId);
        mProgressDialogue = new ProgressDialog(this);
        mProgressDialogue.setTitle("Loading Data..");
        mProgressDialogue.setMessage("Please wait");
        mProgressDialogue.setCanceledOnTouchOutside(false);
        mProgressDialogue.show();
        mState ="not_friends";
        mFriendreqDatabase = FirebaseDatabase.getInstance().getReference().child("friend_req");
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        FriendDatabase = FirebaseDatabase.getInstance().getReference().child("friends");
        decline_Button = findViewById(R.id.decline_friendreq);
        decline_Button.setOnClickListener(this);
        mNotificatiodatabase = FirebaseDatabase.getInstance().getReference().child("notifications");



        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String DisplayName = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();
                mDisplayName.setText(DisplayName);
                Picasso.with(Profile_View_activity.this).load(image).placeholder(R.drawable.icon_imageview).into(imageView);

                //------friends List request Feature-----

                mFriendreqDatabase.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(UserId)){
                            String req_child =dataSnapshot.child(UserId).child("req_type").getValue().toString();
                            if(req_child.equals("received")){

                                mState = "req_received";
                                sendreq_button.setText("Accept Friend Request");
                                decline_Button.setVisibility(View.VISIBLE);
                                decline_Button.setEnabled(true);

                            }else if(req_child.equals("sent")){

                                mState = "req_sent";
                                sendreq_button.setText("Cancel Friend Request");
                                decline_Button.setVisibility(View.INVISIBLE);
                                decline_Button.setEnabled(false);
                            }

                        }else{
                            FriendDatabase.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.hasChild(UserId)){

                                        mState = "friends";
                                        sendreq_button.setText("Unfriend");
                                        decline_Button.setVisibility(View.INVISIBLE);
                                        decline_Button.setEnabled(false);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                        mProgressDialogue.dismiss();


                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });





            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v==sendreq_button){
            sendreq_button.setEnabled(false);
            //--------not friends state--------
            if(mState.equals("not_friends")){
                mFriendreqDatabase.child(mCurrentUser.getUid()).child(UserId).child("req_type").setValue("sent")
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            mFriendreqDatabase.child(UserId).child(mCurrentUser.getUid()).child("req_type").setValue("received")
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    HashMap<String, String> notificatondata = new HashMap<>();
                                    notificatondata.put("From" , mCurrentUser.getUid());
                                    notificatondata.put("Type", "Request");



                                    mNotificatiodatabase.child(UserId).push().setValue(notificatondata)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            sendreq_button.setEnabled(true);
                                            mState = "req_sent";
                                            sendreq_button.setText("Cancel Request");
                                            decline_Button.setVisibility(View.INVISIBLE);
                                            decline_Button.setEnabled(false);

                                        }
                                    });


//                                    Toast.makeText(getApplicationContext(), "Request Sent" , Toast.LENGTH_LONG).show();

                                }
                            });

                        }
                        else{
                            Toast.makeText(getApplicationContext(), "Not working" , Toast.LENGTH_LONG).show();
                        }

                    }
                });

            }
            //------ Cancel req state--------
            if(mState.equals("req_sent")){
                mFriendreqDatabase.child(mCurrentUser.getUid()).child(UserId).removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mFriendreqDatabase.child(UserId).child(mCurrentUser.getUid()).removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                sendreq_button.setEnabled(true);
                                mState = "not_friends";
                                sendreq_button.setText("Send Friend Request");
                                decline_Button.setVisibility(View.INVISIBLE);
                                decline_Button.setEnabled(false);
//                                Toast.makeText(getApplicationContext(), "Request Sent" , Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
            }
            //----- REQ Received State---
            if(mState.equals("req_received")){

                final String currentDate = DateFormat.getDateTimeInstance().format(new Date());

                FriendDatabase.child(mCurrentUser.getUid()).child(UserId).child("date").setValue(currentDate)
                        .addOnSuccessListener(v new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        FriendDatabase.child(UserId).child(mCurrentUser.getUid()).child("date").setValue(currentDate)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                mFriendreqDatabase.child(mCurrentUser.getUid()).child(UserId).removeValue()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                mFriendreqDatabase.child(UserId).child(mCurrentUser.getUid()).removeValue()
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                sendreq_button.setEnabled(true);
                                                                mState = "friends";
                                                                sendreq_button.setText("Unfriend");
                                                                decline_Button.setVisibility(View.INVISIBLE);
                                                                decline_Button.setEnabled(false);
//                                                                Toast.makeText(getApplicationContext(), "Request Sent" , Toast.LENGTH_LONG).show();
                                                            }
                                                        });
                                            }
                                        });

                            }
                        });

                    }
                });

            }
            if(mState.equals("friends")){
                FriendDatabase.child(mCurrentUser.getUid()).child(UserId).removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                FriendDatabase.child(UserId).child(mCurrentUser.getUid()).removeValue()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                sendreq_button.setEnabled(true);
                                                mState = "not_friends";
                                                sendreq_button.setText("Send Friend Request");
                                                decline_Button.setVisibility(View.INVISIBLE);
                                                decline_Button.setEnabled(false);
//                                                Toast.makeText(getApplicationContext(), "Request Sent" , Toast.LENGTH_LONG).show();
                                            }
                                        });
                            }
                        });

            }



        }


    }
}
