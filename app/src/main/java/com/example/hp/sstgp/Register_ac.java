package com.example.hp.sstgp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register_ac extends AppCompatActivity {

    public void open2(){
        Intent intent= new Intent(this,Main2Activity.class);
        startActivity(intent);
    }
    private EditText emailText ,userNameText;
    private EditText passwordText;
    private TextView TextViewSignUp;
    private ProgressDialog progressDialogue;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;

    public void registerUser(){
        final String email= emailText.getText().toString().trim();
        String password= passwordText.getText().toString().trim();
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "please enter email", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password)){
             Toast.makeText(this, "Please enter password",Toast.LENGTH_SHORT).show();
             return;
        }
        progressDialogue.setMessage("Registering");
        progressDialogue.show();

        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {

                            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                            String uid = currentUser.getUid();
                            String userName = userNameText.getText().toString();

                            mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

                            UserInformation userInfo = new UserInformation(userName,email,"Default","Default","true");


                            mDatabase.setValue(userInfo);



                            progressDialogue.dismiss();
                            Toast.makeText(Register_ac.this, "Registered", Toast.LENGTH_SHORT).show();
                        } else{
                            progressDialogue.dismiss();
                            Toast.makeText(Register_ac.this, "Couldnt register:(",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_ac);
        Button RegisterButton=(Button) findViewById(R.id.RegisterButton);
        userNameText = findViewById(R.id.editText2);

        emailText= (EditText) findViewById(R.id.editText3) ;
        passwordText = (EditText) findViewById(R.id.registerPass);
        progressDialogue= new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();

        RegisterButton.setOnClickListener (
                new Button.OnClickListener(){
                    public void onClick(View v){

                        registerUser();

                    }
                }
        );
    }
}
