package com.example.miniproject_yogify;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

public class Workout extends AppCompatActivity implements workout_page_adapter.portfoliopage_viewholder.onClickRecyclerListen{

    private ArrayList<workout_data> workoutdata;
    private workout_page_adapter workoutAdapter;
    RecyclerView recycler_workout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        int autoworkout = Integer.parseInt(intent.getStringExtra("AUTOWORKOUTINDEX"));
        Log.e("AUTOWORKOUT",Integer.toString(autoworkout));
        if(autoworkout>=0){
            AutoWorkout(autoworkout);
        }
        setContentView(R.layout.activity_workout);

        recycler_workout = findViewById(R.id.recyclyer_workout);
        workoutdata = new ArrayList<>();
        loadReferenceWorkout();
        workoutAdapter = new workout_page_adapter(this, workoutdata, this);
        recycler_workout.setAdapter(workoutAdapter);
        recycler_workout.setLayoutManager(new LinearLayoutManager(this));

    }

    public void AutoWorkout(int autoworkoutindex){
        Intent intent = new Intent(this,PoseDetectStart.class);
        intent.putExtra("WORKOUTINDEX",Integer.toString(autoworkoutindex));
        intent.putExtra("AUTODETECT","FALSE");
        startActivity(intent);
        finish();
    }

    public void loadReferenceWorkout(){
        for(int i=0;i<MainActivity.ref_angles.size();i++){
            workoutdata.add(new workout_data(MainActivity.ref_angles.get(i).getName(),30));
        }
    }

    @Override
    public void onClickRecycler(int position, View v) {
        Intent intent = new Intent(this,PoseDetectStart.class);
        intent.putExtra("AUTODETECT","FALSE");
        Log.e("POSITION RECYCLER",Integer.toString(position));
        intent.putExtra("WORKOUTINDEX",Integer.toString(position));
        startActivity(intent);


    }

}