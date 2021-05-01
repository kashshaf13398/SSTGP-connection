package com.example.hp.sstgp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Feed_screen extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener{

    private Button LoutButton;
    private TextView Dum;
//    private android.support.v7.widget.Toolbar testToolbar;
    private FirebaseAuth firebaseAuth;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;

    private Button Location,save,all_user;
    private EditText Name;
    private EditText Address;
    private DatabaseReference databaseReference;
    private FirebaseUser fuser;

    //private DataBa


    public void open1(){
        Intent intent= new Intent(this,Main2Activity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_feed_screen);


          setContentView(R.layout.home_page);
          mDrawerLayout= (DrawerLayout) findViewById(R.id.drawer_layout);
          NavigationView navigationView = findViewById(R.id.nav_view);
          navigationView.setNavigationItemSelectedListener(this);
          mToggle = new ActionBarDrawerToggle(this, mDrawerLayout , R.string.open ,R.string.close);
          mDrawerLayout.addDrawerListener(mToggle);
          mToggle.syncState();
          getSupportActionBar().setDisplayHomeAsUpEnabled(true);
          fuser = FirebaseAuth.getInstance().getCurrentUser();

          if(savedInstanceState==null) {
              getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Message_fragment()).commit();
              navigationView.setCheckedItem(R.id.nav_message);
          }


//          testToolbar = (Toolbar) findViewById(R.id.home_page_toolbar);
//          setSupportActionBar(testToolbar);
//          getSupportActionBar().setTitle("Sakif");

//        Name= findViewById(R.id.editText);
//        Address=findViewById(R.id.editText4);
//        LoutButton = findViewById(R.id.Logout);
//        LoutButton.setOnClickListener(this);
//        Location =findViewById(R.id.Location);
//        Location.setOnClickListener(this);
//        Dum= findViewById(R.id.loginac);
        firebaseAuth= FirebaseAuth.getInstance();
//        save= findViewById(R.id.button);
//        save.setOnClickListener(this);
//        all_user=findViewById(R.id.all_users_button);
//        all_user.setOnClickListener(this);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        if(firebaseAuth.getCurrentUser()==null){
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }
        FirebaseUser user = firebaseAuth.getCurrentUser();
//
        //Dum.setText("Welcome "+user.getEmail());



    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){

            case R.id.nav_message:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container , new Message_fragment()).commit();
                break;

            case R.id.nav_account:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container , new User_profile_fragment()).commit();
                break;

            case R.id.nav_all_users:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container , new All_users_fragment()).commit();
                break;
            case R.id.nav_friend_request:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container , new Friend_request_fragment()).commit();
                break;
            case R.id.nav_log_out:
                finish();
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this,MainActivity.class));
                break;

        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void userSaveInformation(){
        String name=Name.getText().toString().trim();
        String add= Address.getText().toString().trim();

        UserInformation userInfo = new UserInformation(name,add);

        FirebaseUser user = firebaseAuth.getCurrentUser();

        databaseReference.child("Users").child(user.getUid()).setValue(userInfo);

        Toast.makeText(this, "saven info", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onClick(View v) {
        if(v==LoutButton){
            finish();
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this,MainActivity.class));
        }
        if(v==Location){
            open1();
        }
        if(v==save){
            userSaveInformation();

        }
        if (v==all_user){
            finish();
            startActivity(new Intent(this,All_User_Activity.class));
        }
    }

    @Override
    protected void onStart() {

        super.onStart();
        databaseReference.child("Users").child(fuser.getUid()).child("online").setValue("true");
    }

    @Override
    protected void onStop() {
        super.onStop();
        databaseReference.child("Users").child(fuser.getUid()).child("online").setValue("false");
    }
}
