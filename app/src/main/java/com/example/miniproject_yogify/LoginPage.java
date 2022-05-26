package com.example.miniproject_yogify;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
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

public class LoginPage extends AppCompatActivity {

    TextInputLayout phoneno,password;
    Button login,login_forgetpassword,login_signup;
    int autologin=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login_page);

        //HOOKS
        phoneno=findViewById(R.id.login_phoneno);
        password=findViewById(R.id.login_password);
        login=findViewById(R.id.login_login);
        login_forgetpassword=findViewById(R.id.login_forgetpassword);
        login_signup=findViewById(R.id.login_signup);


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser(v);
            }
        });
        login_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginPage.this, SignUpPage.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void AutoSave(String save_phoneno,String save_password)
    {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("phoneno",save_phoneno);
        editor.putString("password",save_password);
        editor.apply();
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
              //  "(?=.*[0-9])" +         //at least 1 digit
                //"(?=.*[a-z])" +         //at least 1 lower case letter
               // "(?=.*[A-Z])" +         //at least 1 upper case letter
               // "(?=.*[a-zA-Z])" +      //any letter
               // "(?=.*[@#$%^&+=])" +    //at least 1 special character
                "(?=\\S+$)" +           //no white spaces
                ".{4,}" +               //at least 4 characters
                "$";
        if(val.isEmpty()){
            password.setError("Field cannot be empty");
            return false;
        }else if (!val.matches(passwordVal)) {
            password.setError("Password should be more than 4 characters");
            //password.setError("Password is too weak\n•at least 1 upper case letter\n•at least 1 lower case letter\n•at least 1 special character\n•at least 1 digit");
            return false;}
        else{
            password.setError(null);
            password.setErrorEnabled(false);
            return true;
        }
    }

    public void loginUser(View view){
        if(!validatePhoneNo() | !validatePassword()){
            return;
        }
        else{
            String login_phoneno = phoneno.getEditText().getText().toString().trim();
            String login_password = password.getEditText().getText().toString().trim();
            checkUser(login_phoneno,login_password);
        }
    }

    private void checkUser(String login_phoneno,String login_password) {

        DatabaseReference reference = FirebaseDatabase.getInstance("https://miniprojectyogify-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("users");

        Query checkUsername= reference.orderByChild("phoneno").equalTo(login_phoneno);
        checkUsername.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    phoneno.setError(null);
                    phoneno.setErrorEnabled(false);
                    String database_password = snapshot.child(login_phoneno).child("password").getValue(String.class);

                    if(database_password.equals(login_password))
                    {
                        password.setError(null);
                        password.setErrorEnabled(false);
                        AutoSave(login_phoneno,login_password);
                        Intent intent = new Intent(LoginPage.this,Home.class);
                        intent.putExtra("phoneno",login_phoneno);
                        intent.putExtra("startingpage","1");
                        startActivity(intent);
                        finish();
                    }
                    else{
                        password.setError("Incorrect Password");
                        password.requestFocus();
                    }
                }
                else{
                    phoneno.setError("User does not exists");
                    phoneno.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}