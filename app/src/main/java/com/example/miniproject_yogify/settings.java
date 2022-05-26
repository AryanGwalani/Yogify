package com.example.miniproject_yogify;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class settings extends Fragment {
    MaterialButton funds,settings,support,logout;
    TextView username;
    TextView fullname;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.settings,container,false);
        funds = view.findViewById(R.id.settings_button_funds);
        support = view.findViewById(R.id.settings_button_support);
        logout=view.findViewById(R.id.logout);
        username=view.findViewById(R.id.settings_username);
        fullname=view.findViewById(R.id.settings_fullname);

        getDetails();
        onClickListeners(view);

        return view;
    }
    public void logout()
    {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("phoneno",null);
        editor.putString("password",null);
        editor.apply();
        Intent intent = new Intent(getActivity(),LoginPage.class);
        startActivity(intent);
    }
    public void getDetails()
    {
        Intent getData= getActivity().getIntent();
        String phoneno = getData.getStringExtra("phoneno");
        Log.d("Phoneno",phoneno);
        DatabaseReference reference = FirebaseDatabase.getInstance("https://miniprojectyogify-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String data_username = snapshot.child(phoneno).child("username").getValue(String.class);
                String data_fullname = snapshot.child(phoneno).child("fullname").getValue(String.class);
                username.setText(data_username);
                fullname.setText(data_fullname);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    void onClickListeners(View v){
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

    }
}
