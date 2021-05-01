package com.example.hp.sstgp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class Friend_request_fragment extends Fragment {
    private RecyclerView mUsers_list;
    private DatabaseReference mDatabaseReference,mUserDatabaseRef;
    private FragmentActivity fragmentActivity;
    private FirebaseUser mCurrentUser;
    private  String userName,thumbImage,email;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_friend_request, container ,false);


        mCurrentUser= FirebaseAuth.getInstance().getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("friends").child(mCurrentUser.getUid() );
        mUserDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsers_list= (RecyclerView) v.findViewById(R.id.frnds_recylerview);
        mUsers_list.setHasFixedSize(true);
        mUsers_list.setLayoutManager(new LinearLayoutManager(getActivity()));

        return v;
    }
    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<UserInformation, Frnd_UsersViewHolder> firebaseRecyclerAdapter_
                = new FirebaseRecyclerAdapter<UserInformation, Frnd_UsersViewHolder>(
                UserInformation.class,
                R.layout.all_user_single_layout,
                Frnd_UsersViewHolder.class,
                mDatabaseReference
        ) {
            @Override
            protected void populateViewHolder( final Frnd_UsersViewHolder viewHolder, final UserInformation model, int position) {



                final String uid = getRef(position).getKey();



                mUserDatabaseRef.child(uid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                         userName= dataSnapshot.child("name").getValue().toString();
                         thumbImage= dataSnapshot.child("thumbImage").getValue().toString();
                         email = dataSnapshot.child("address").getValue().toString();
                         viewHolder.setName(userName);
                         viewHolder.setEmail(email);
                         viewHolder.setImage(thumbImage , getContext());

                        viewHolder._mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                CharSequence options[] = new CharSequence []{"Open profile"," Send Message"};
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setTitle("Select Options");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if(which == 0){
                                            Intent profileIntent = new Intent(getActivity(), Profile_View_activity.class);
                                            profileIntent.putExtra("User ID" , uid);
                                            startActivity(profileIntent);

                                        }
                                        else if(which==1){
                                            Intent chatIntent = new Intent(getActivity(), Chat_Activity.class);
                                            chatIntent.putExtra("User ID" , uid);
                                            chatIntent.putExtra("username" , userName);
                                            startActivity(chatIntent);
                                        }

                                    }
                                });

                                builder.show();






                            }
                        });


                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

//                viewHolder._mView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Intent profileIntent = new Intent(getActivity(), Profile_View_activity.class);
//                        profileIntent.putExtra("User ID" , uid);
//                        startActivity(profileIntent);
//
//                    }
//                });

            }
        };
        mUsers_list.setAdapter(firebaseRecyclerAdapter_);
    }
    public static class Frnd_UsersViewHolder extends RecyclerView.ViewHolder {

        View _mView;
        public Frnd_UsersViewHolder(@NonNull View itemView) {
            super(itemView);
            _mView = itemView;
        }
        public void setName(String name){
            TextView userNameView_ = _mView.findViewById(R.id.all_user_textView);
            userNameView_.setText(name);
        }
        public void setEmail(String name){
            TextView userNameView_ = _mView.findViewById(R.id.all_user_textView2);
            userNameView_.setText(name);
        }
        public void setImage(String image , Context ctx){

            ImageView imageView_ = _mView.findViewById(R.id.imageView3);
            Picasso.with(ctx).load(image).placeholder(R.drawable.icon_imageview).into(imageView_);

        }
    }
}
