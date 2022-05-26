package com.example.miniproject_yogify;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class SignUpPage extends AppCompatActivity {

    TextInputLayout fullname,username,phoneno,password;
    Button signup,signup_login;

    FirebaseDatabase rootNode;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_page);

        fullname=findViewById(R.id.signup_fullname);
        username=findViewById(R.id.signup_username);
        phoneno =findViewById(R.id.signup_phone);
        password=findViewById(R.id.signup_password);
        signup=findViewById(R.id.signup_signup);
        signup_login=findViewById(R.id.signup_login);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootNode=FirebaseDatabase.getInstance("https://miniprojectyogify-default-rtdb.asia-southeast1.firebasedatabase.app");
                reference=rootNode.getReference("users");
                registerUser(v);
            }
        });

        signup_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpPage.this,LoginPage.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public Boolean validateName(){
        String val = fullname.getEditText().getText().toString();
        if(val.isEmpty()){
            fullname.setError("Field cannot be empty");
            return false;
        }
        else{
            fullname.setError(null);
            return true;
        }
    }
    public Boolean validateUsername(){
        String val = username.getEditText().getText().toString();
        String noWhiteSpace = "\\A\\w{4,20}\\z";
        if(val.isEmpty()){
            username.setError("Field cannot be empty");
            return false;
        }else if(val.length()>=15){
            username.setError("Username too long");
            return false;
        }else if (!val.matches(noWhiteSpace)) {
            username.setError("White Spaces are not allowed");
            return false;}
        else{
            username.setError(null);
            username.setErrorEnabled(false);
            return true;
        }
    }
    public Boolean validatePhoneNo(){
        String val = phoneno.getEditText().getText().toString();
        if(val.isEmpty()){
            phoneno.setError("Field cannot be empty");
            return false;
        }else if(val.length()!=10){
            //Log.d("LENGTH",Integer.toString(val.length()));
            phoneno.setError("Incorrect Phone No");
            return false;
        }
        else{
            phoneno.setError(null);
            phoneno.setErrorEnabled(false);
            return true;
        }
    }
    public Boolean validatePassword(){
        String val = password.getEditText().getText().toString();
        String passwordVal = "^" +
                //"(?=.*[0-9])" +         //at least 1 digit
                //"(?=.*[a-z])" +         //at least 1 lower case letter
                //"(?=.*[A-Z])" +         //at least 1 upper case letter
                //"(?=.*[a-zA-Z])" +      //any letter
                //"(?=.*[@#$%^&+=])" +    //at least 1 special character
                "(?=\\S+$)" +           //no white spaces
                ".{4,}" +               //at least 4 characters
                "$";
        if(val.isEmpty()){
            password.setError("Field cannot be empty");
            return false;
        }else if (!val.matches(passwordVal)) {
            password.setError("Password should be more than 4 characters");
           // password.setError("Password is too weak\n•at least 1 upper case letter\n•at least 1 lower case letter\n•at least 1 special character\n•at least 1 digit");
            return false;}
        else{
            password.setError(null);
            password.setErrorEnabled(false);
            return true;
        }
    }


    public void registerUser(View view){
        String regname = fullname.getEditText().getText().toString();
        String regusername = username.getEditText().getText().toString().toLowerCase(Locale.ROOT);
        String regphoneno = phoneno.getEditText().getText().toString().toLowerCase(Locale.ROOT);
        String regpassword = password.getEditText().getText().toString();
        if(!validateName() | !validatePassword() | !validatePhoneNo()  | !validateUsername()){
            return;
        }
        Query checkPhoneno = reference.orderByChild("phoneno").equalTo(regphoneno);
        checkPhoneno.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    phoneno.setError("Phone No already in use");
                    phoneno.requestFocus();
                    username.setError(null);
                    username.setErrorEnabled(false);

                }
                else{
                    phoneno.setError(null);
                    phoneno.setErrorEnabled(false);
                    Query checkUsername = reference.orderByChild("username").equalTo(regusername);
                    checkUsername.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                username.setError("Username already exists");
                                username.requestFocus();
                            }
                            else{
                                username.setError(null);
                                username.setErrorEnabled(false);
                                user_signup user_signup = new user_signup(regname,regpassword,regusername,regphoneno);
                                reference.child(regphoneno).setValue(user_signup);
                                Intent intent = new Intent(SignUpPage.this,Home.class);
                                intent.putExtra("phoneno",regphoneno);
                                intent.putExtra("startingpage","1");
                                startActivity(intent);
                                finish();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) { }
                    });

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}