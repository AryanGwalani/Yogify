package com.example.miniproject_yogify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_menu);
        int startscreen=0;
        Log.d("START SCREEN",Integer.toString(startscreen));
        switch (startscreen){
            case 1: getSupportFragmentManager().beginTransaction().replace(R.id.fragmentLayout, new mainmenu()).commit();
                break;
            case 2: getSupportFragmentManager().beginTransaction().replace(R.id.fragmentLayout, new settings()).commit();
                break;
            default:
                Log.d("ERROR","SCREEN START IS DEFAULT");
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentLayout, new mainmenu()).commit();

        }


        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        //Intent intent = new Intent(this,PoseDetectStart.class);
        //startActivity(intent);
        //finish();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;

            switch (item.getItemId()){
                case R.id.home:
                  //  PoseDetectStart.handler.removeCallbacks(PoseDetectStart.runnable);
                    selectedFragment = new mainmenu();
                    break;
                case R.id.profile:
                    selectedFragment=new settings();

            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentLayout,selectedFragment).commit();

            return true;
        }
    };
}