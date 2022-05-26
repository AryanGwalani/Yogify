 package com.example.miniproject_yogify;

 import android.Manifest;
 import android.content.Intent;
 import android.content.SharedPreferences;
 import android.content.pm.PackageManager;
 import android.os.Bundle;
 import android.os.Handler;
 import android.speech.RecognitionListener;
 import android.speech.RecognizerIntent;
 import android.speech.SpeechRecognizer;
 import android.speech.tts.TextToSpeech;
 import android.speech.tts.UtteranceProgressListener;
 import android.util.Log;
 import android.view.View;
 import android.view.Window;
 import android.view.WindowManager;
 import android.widget.AdapterView;
 import android.widget.CompoundButton;
 import android.widget.TextView;
 import android.widget.Toast;
 import android.widget.ToggleButton;

 import androidx.annotation.NonNull;
 import androidx.appcompat.app.AppCompatActivity;
 import androidx.core.app.ActivityCompat;
 import androidx.core.content.ContextCompat;
 import androidx.lifecycle.MutableLiveData;
 import androidx.lifecycle.Observer;


 import com.example.miniproject_yogify.posedetector.PoseDetectorProcessor;
 import com.example.miniproject_yogify.preference.PreferenceUtils;
 import com.google.firebase.database.DataSnapshot;
 import com.google.firebase.database.DatabaseError;
 import com.google.firebase.database.DatabaseReference;
 import com.google.firebase.database.FirebaseDatabase;
 import com.google.firebase.database.Query;
 import com.google.firebase.database.ValueEventListener;
 import com.google.mlkit.vision.pose.Pose;
 import com.google.mlkit.vision.pose.PoseDetection;
 import com.google.mlkit.vision.pose.PoseDetector;
 import com.google.mlkit.vision.pose.PoseDetectorOptionsBase;
 import com.google.mlkit.vision.pose.PoseLandmark;

 import java.io.IOException;
 import java.lang.reflect.Array;
 import java.util.ArrayList;
 import java.util.List;
 import java.util.Locale;

 public class MainActivity extends AppCompatActivity{

     public static ArrayList<referencedata_type> ref_angles;
     public static String auto_phoneno;
     public static int CameraFacing=1;

     /** Called when the activity is first created. */
     @Override
     public void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         requestWindowFeature(Window.FEATURE_NO_TITLE);
         getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

         setContentView(R.layout.activity_main);

         while(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED){
             ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO},1);
         }
         while(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
             ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},1);
         }
         while(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
             ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
         }
         while(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
             ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
         }
         ref_angles = new ArrayList<>();

         DatabaseReference reference = FirebaseDatabase.getInstance("https://miniprojectyogify-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("data");

         reference.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot snapshot) {
                 if(snapshot.exists()){
                     Log.d("LOAD SUCCESS","YES");
                     Log.d("NO OF ASANAS",Long.toString(snapshot.getChildrenCount()));
                     for(int i=0;i<snapshot.getChildrenCount();i++){
                         DataSnapshot snp = snapshot.child(Integer.toString(i));
                         ref_angles.add(new referencedata_type(snp.child("name").getValue().toString(),snp.child("left_elbow_angle").getValue(Integer.class),snp.child("left_knee_angle").getValue(Integer.class),snp.child("left_shoulder_angle").getValue(Integer.class),snp.child("right_elbow_angle").getValue(Integer.class),snp.child("right_knee_angle").getValue(Integer.class),snp.child("right_shoulder_angle").getValue(Integer.class)));
                     }
                 }
             }
             @Override
             public void onCancelled(@NonNull DatabaseError error) {

             }
         });

         new Handler().postDelayed(new Runnable() {
             @Override
             public void run() {
                 AutoLogin();
             }
         },1500);
     }

     @Override
     public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
         super.onRequestPermissionsResult(requestCode, permissions, grantResults);
         if(requestCode==1){
             if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                 Toast.makeText(this,"PERMISSION GRANTED",Toast.LENGTH_SHORT);
             }
         }
     }
     private void AutoLogin()
     {
         SharedPreferences sharedPreferences = getSharedPreferences("shared preferences",MODE_PRIVATE);

         auto_phoneno = sharedPreferences.getString("phoneno","");
         String auto_password = sharedPreferences.getString("password","");

         Log.d("saved phoneno",auto_phoneno);
         Log.d("saved password",auto_password);
         DatabaseReference reference = FirebaseDatabase.getInstance("https://miniprojectyogify-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("users");

         Query checkUsername= reference.orderByChild("phoneno").equalTo(auto_phoneno);
         checkUsername.addListenerForSingleValueEvent(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot snapshot) {
                 if(snapshot.exists())
                 {
                     String database_password = snapshot.child(auto_phoneno).child("password").getValue(String.class);

                     if(database_password.equals(auto_password))
                     {
                         //Intent intent = new Intent(MainActivity.this,testing_page.class);
                         Intent intent = new Intent(MainActivity.this,Home.class);
                         intent.putExtra("phoneno",auto_phoneno);
                         intent.putExtra("startingpage","1");
                         startActivity(intent);
                         finish();
                     }
                     else{
                         Intent intent = new Intent(MainActivity.this, LoginPage.class);
                         startActivity(intent);
                         finish();
                     }
                 }
                 else{
                     Intent intent = new Intent(MainActivity.this, LoginPage.class);
                     startActivity(intent);
                     finish();
                 }
             }
             @Override
             public void onCancelled(@NonNull DatabaseError error) {
             }
         });

     }

 }
