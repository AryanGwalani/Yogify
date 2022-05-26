package com.example.miniproject_yogify;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class mainmenu extends Fragment{


    @Nullable
    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mainmenu,container,false);

        Intent getData= getActivity().getIntent();

        RelativeLayout autotrackworkout = view.findViewById(R.id.btn_autotrackworkout);
        RelativeLayout manualworkout = view.findViewById(R.id.btn_manualworkout);
        autotrackworkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("ONCLICK","PRESSED");
                Intent intent = new Intent(getContext(),PoseDetectStart.class);
                intent.putExtra("AUTODETECT","TRUE");
                startActivity(intent);
            }
        });

        manualworkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),Workout.class);
                intent.putExtra("AUTOWORKOUTINDEX","-1");
                startActivity(intent);
            }
        });



        return view;
    }

   /* void saveFireBaseData()
    {
        Log.d("SAVING","DATA");
        FirebaseDatabase rootNode;
        DatabaseReference reference;
        rootNode=FirebaseDatabase.getInstance("https://startup-ee05d-default-rtdb.asia-southeast1.firebasedatabase.app");
        reference=rootNode.getReference("users");
        reference.child(phoneno).child("data").child(watchlist_count.get(CURRENT_watchlistno_position)).setValue(watchlist_crypto);
    }*/

}
