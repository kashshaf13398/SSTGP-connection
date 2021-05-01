package com.example.hp.sstgp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.util.Log;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity {
    String s="Sakif";
    public static final String TAG="SakifsMessage";
    private android.support.v7.widget.Toolbar mToolbar;

    private ProgressDialog progressDialogue;
    private FirebaseAuth firebaseAuth;
    private TextView mEmailView;
    private TextView mPasswordView;
    public void open1(){
        Intent intent= new Intent(this,Register_ac.class);
        startActivity(intent);
    }
    public void open3(){
        Intent intent= new Intent(this,Main3Activity.class);
        startActivity(intent);
    }

    public void open2(){
        Intent intent= new Intent(this,Main2Activity.class);
        startActivity(intent);
    }
    private void userlogin(){
        String email= mEmailView.getText().toString().trim();
        String password= mPasswordView.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "please enter email", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please enter password",Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialogue.setMessage("Signing in");
        progressDialogue.show();

        firebaseAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialogue.dismiss();
                        if(task.isSuccessful()){
                            finish();
                            startActivity(new Intent(getApplicationContext(), Feed_screen.class));
                        }else{
                            Toast.makeText(MainActivity.this, "Not Registered", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseAuth = FirebaseAuth.getInstance();
//        mToolbar =  findViewById(R.id.main_page_toolbar);
//        setSupportActionBar(mToolbar);
//        getSupportActionBar().setTitle("YOO");

        progressDialogue= new ProgressDialog(this);
        if(firebaseAuth.getCurrentUser()!=null){
            finish();
            startActivity(new Intent(this, Feed_screen.class));
        }
        mEmailView = findViewById(R.id.editText7);
        mPasswordView=findViewById(R.id.editText6);
        Button sakifsButton=(Button) findViewById(R.id.sakifsButton);
        sakifsButton.setOnClickListener (
            new Button.OnClickListener(){
                public void onClick(View v){
                    userlogin();
                }
            }
            );
        TextView sakifsText= (TextView) findViewById(R.id.sakifsText);
        sakifsText.setOnClickListener(
                new TextView.OnClickListener(){

                    @Override
                    public void onClick(View v) {
                        open1();
                    }
                }
        );
        TextView sakifsText2= (TextView) findViewById(R.id.signinor);
        sakifsText2.setOnClickListener(
                new TextView.OnClickListener(){

                    @Override
                    public void onClick(View v) {
                        open3();
                    }
                }
        );
        Log.i(TAG,"SakifsMessage");
    }
}
