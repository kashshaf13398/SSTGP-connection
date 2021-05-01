package com.example.hp.sstgp;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class All_users_fragment extends Fragment {
    RecyclerView mUsers_list;
    private DatabaseReference mDatabaseReference;
    private FragmentActivity fragmentActivity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        View v;

        v= inflater.inflate(R.layout.fragment_all_users, container ,false);

        mUsers_list= (RecyclerView) v.findViewById(R.id.all_users_list);
        mUsers_list.setHasFixedSize(true);
        mUsers_list.setLayoutManager(new LinearLayoutManager(fragmentActivity));


        return v;


    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<UserInformation, All_UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<UserInformation, All_UsersViewHolder>(
                UserInformation.class,
                R.layout.all_user_single_layout,
                All_UsersViewHolder.class,
                mDatabaseReference
        ) {
            @Override
            protected void populateViewHolder(All_UsersViewHolder viewHolder, UserInformation model, int position) {

                viewHolder.setName(model.getName());
                viewHolder.setEmail(model.getAddress());
                viewHolder.setImage(model.getThumbImage(), getContext());
                final String uid = getRef(position).getKey();
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent profileIntent = new Intent(getActivity(), Profile_View_activity.class);
                        profileIntent.putExtra("User ID" , uid);
                        startActivity(profileIntent);

                    }
                });

            }
        };
        mUsers_list.setAdapter(firebaseRecyclerAdapter);
    }
    public static class All_UsersViewHolder extends RecyclerView.ViewHolder {

        View mView;
        public All_UsersViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void setName(String name){
            TextView userNameView = mView.findViewById(R.id.all_user_textView);
            userNameView.setText(name);
        }
        public void setEmail(String name){
            TextView userNameView = mView.findViewById(R.id.all_user_textView2);
            userNameView.setText(name);
        }
        public void setImage(String image , Context ctx){

            ImageView imageView = mView.findViewById(R.id.imageView3);
            Picasso.with(ctx).load(image).placeholder(R.drawable.icon_imageview).into(imageView);

        }
    }
}
