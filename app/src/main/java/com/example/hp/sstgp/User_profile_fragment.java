package com.example.hp.sstgp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Random;

import id.zelory.compressor.Compressor;

public class User_profile_fragment extends Fragment implements View.OnClickListener {
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;
    private ImageView imageView;
    private TextView displayName;
    private EditText changeText;
    private Button changeName,changeImage;
    private static final int GALLERY_PICK = 1;
    private ProgressDialog mpr;

    private StorageReference mImageRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v= inflater.inflate(R.layout.fragment_user_profile, container ,false);

        displayName = v.findViewById(R.id.display_name);
        changeName = v.findViewById(R.id.change_name_button);
        changeName.setOnClickListener(this);
        changeImage = v.findViewById(R.id.change_image);
        changeImage.setOnClickListener(this);
        changeText = v.findViewById(R.id.change_name_editText);
        imageView =v.findViewById(R.id.imageView);

        mImageRef = FirebaseStorage.getInstance().getReference();


        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        String uid = firebaseUser.getUid();
        final String email = firebaseUser.getEmail();
        databaseReference= FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name= dataSnapshot.child("name").getValue().toString();
                String image= dataSnapshot.child("image").getValue().toString();
                displayName.setText(name);

                if(!image.equals("Default")) {

                    Picasso.with(getActivity()).load(image).placeholder(R.drawable.icon_imageview).into(imageView);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return v;
    }

    @Override
    public void onClick(View v) {
        if(v==changeName){
            databaseReference.child("name").setValue(changeText.getText().toString());
            displayName.setText(changeText.getText().toString());
        }
        if(v==changeImage){
            Intent galleryIntent = new Intent();
            galleryIntent.setType("image/*");
            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(galleryIntent , "Select Image"), GALLERY_PICK);
            /*
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(getActivity());
                    */
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICK && resultCode == getActivity().RESULT_OK) {

            Uri image_uri = data.getData();

            CropImage.activity(image_uri).setAspectRatio(1, 1)
                    .start(getContext(), this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == getActivity().RESULT_OK) {

                mpr = new ProgressDialog(getActivity());
                mpr.setTitle("Uploading image");
                mpr.setMessage("Please Wait..");
                mpr.setCanceledOnTouchOutside(false);
                mpr.show();

                Uri resultUri = result.getUri();

                File thumb_file = new File(resultUri.getPath());

                String cUID = firebaseUser.getUid().toString();

                Bitmap thumb_bitmap = new Compressor(getActivity())
                        .setMaxHeight(200)
                        .setMaxWidth(200)
                        .compressToBitmap(thumb_file);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                final byte[] thumb_byte = baos.toByteArray();

                StorageReference filePath = mImageRef.child("profile_images").child(cUID+".jpg");
                final StorageReference thum_storage_filePath = mImageRef.child("profile_images").child("thumbs").child(cUID+".jpg");
                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){

                            task.getResult().getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    final String imagedownUrl = uri.toString();

                                    Picasso.with(getActivity()).load(uri.toString()).placeholder(R.drawable.icon_imageview).into(imageView);

                                    UploadTask uploadTask = thum_storage_filePath.putBytes(thumb_byte);
                                    uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {


                                            if(thumb_task.isSuccessful()){

                                                thumb_task.getResult().getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri thumb_uri) {
                                                        String thumb_image_downUrl= thumb_uri.toString();
                                                        UserInformation info = new UserInformation("Set Name" , firebaseUser.getEmail() , imagedownUrl ,thumb_image_downUrl , "true" );
                                                        databaseReference.setValue(info).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful()){

                                                                    mpr.dismiss();
                                                                    Toast.makeText(getActivity(), "working" , Toast.LENGTH_LONG).show();
                                                                }else{
                                                                    Toast.makeText(getActivity(), "not working" , Toast.LENGTH_LONG).show();
                                                                }
                                                            }
                                                        });

                                                    }
                                                });





                                            }else
                                            {
                                                Toast.makeText(getActivity(), "Thumb not working" , Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });



//                                    databaseReference.child("image").setValue(uri.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                        @Override
//                                        public void onComplete(@NonNull Task<Void> task) {
//                                            if(task.isSuccessful()){
//
//                                                mpr.dismiss();
//                                                Toast.makeText(getActivity(), "working" , Toast.LENGTH_LONG).show();
//                                            }else{
//                                                Toast.makeText(getActivity(), "not working" , Toast.LENGTH_LONG).show();
//                                            }
//                                        }
//                                    });

                                }
                            });


                        }
                        else{
                            Toast.makeText(getActivity(), "not working" , Toast.LENGTH_LONG).show();
                            mpr.dismiss();

                        }
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }
    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(10);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }
}
