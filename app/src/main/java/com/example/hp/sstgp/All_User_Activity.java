package com.example.hp.sstgp;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class All_User_Activity extends AppCompatActivity {

    RecyclerView mUsers_list;
    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all__user_);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsers_list= findViewById(R.id.users_list);
        mUsers_list.setHasFixedSize(true);
        mUsers_list.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<UserInformation, UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<UserInformation, UsersViewHolder>(
                UserInformation.class,
                R.layout.all_user_single_layout,
                UsersViewHolder.class,
                mDatabaseReference
        ) {
            @Override
            protected void populateViewHolder(UsersViewHolder viewHolder, UserInformation model, int position) {

                viewHolder.setName(model.getName());

            }
        };
        mUsers_list.setAdapter(firebaseRecyclerAdapter);


    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder {

        View mView;
        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void setName(String name){
           // TextView userNameView = mView.findViewById(R.id.textView);
            //userNameView.setText(name);
        }
    }
}
