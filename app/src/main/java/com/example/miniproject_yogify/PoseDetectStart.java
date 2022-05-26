package com.example.miniproject_yogify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.miniproject_yogify.posedetector.PoseDetectorProcessor;
import com.example.miniproject_yogify.preference.PreferenceUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.mlkit.vision.pose.PoseDetectorOptionsBase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class PoseDetectStart extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    private static final String POSE_DETECTION = "Pose Detection";

    public static TextView poseresult;
    public static MutableLiveData<Integer> listener;
    private CameraSource cameraSource = null;
    private CameraSourcePreview preview;
    private GraphicOverlay graphicOverlay;
    private String selectedModel = POSE_DETECTION;
    public static TextToSpeech textToSpeech;
    public static TextView workout_start_countdown;
    private TextView workout_start_timer;
    private SpeechRecognizer speechRecognizer;
    public static int c=0,before_start_timer=5;
    public static Thread thread,thread1;
    public int stopthread=0;
    int workout_started_check_1 =0;
    int incorrect_workout_warning=3,correct_workout_appreciate =0;
    int temp_error;
    String autodetectworkout,workoutindex="-1";

    public static Handler handler;
    public static Runnable runnable;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pose_detect_start);

        preview = findViewById(R.id.preview_view);
        graphicOverlay = findViewById(R.id.graphic_overlay);
        poseresult = findViewById(R.id.poseresult);
        workout_start_countdown = findViewById(R.id.workout_start_countdown);
        workout_start_timer = findViewById(R.id.workout_start_timer);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        Intent intent = getIntent();
        autodetectworkout = intent.getStringExtra("AUTODETECT");
        if(autodetectworkout.toUpperCase(Locale.ROOT).equals("FALSE"))workoutindex = intent.getStringExtra("WORKOUTINDEX");
        Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        ToggleButton facingSwitch = findViewById(R.id.facing_switch);
        facingSwitch.setOnCheckedChangeListener(this);

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {

                if (i== TextToSpeech.SUCCESS) {
                    int result = textToSpeech.setLanguage(Locale.UK);

                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Language not supported");
                    } else {
                    }
                } else {
                    Log.e("TTS", "Initialization failed");
                }
            }
        });
        createCameraSource(selectedModel);

        //speechRecognizer
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                if(c==0)temp_error=PoseDetectorProcessor.angle_errors;
                if(PoseDetectorProcessor.angle_errors==temp_error && PoseDetectorProcessor.angle_errors>=0)c=c+1;
                else {
                    c=0;
                    temp_error = PoseDetectorProcessor.angle_errors;
                }
                if(c==5){
                    textToSpeech.speak("ARE YOU DOING THE WORKOUT NAMED "+ MainActivity.ref_angles.get(temp_error).getName(), TextToSpeech.QUEUE_FLUSH,null);
                    (new Handler()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            speechRecognizer.startListening(speechRecognizerIntent);
                            Log.e("SPEECH","STARTED LISTENING");
                        }
                    },3800);
                    (new Handler()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            speechRecognizer.stopListening();
                        }
                    },10000);
                    speechRecognizer.setRecognitionListener(new RecognitionListener() {
                        @Override
                        public void onReadyForSpeech(Bundle bundle) {
                        }

                        @Override
                        public void onBeginningOfSpeech() {
                        }

                        @Override
                        public void onRmsChanged(float v) {

                        }

                        @Override
                        public void onBufferReceived(byte[] bytes) {

                        }

                        @Override
                        public void onEndOfSpeech() {

                        }

                        @Override
                        public void onError(int i) {

                        }

                        @Override
                        public void onResults(Bundle bundle) {
                            ArrayList<String> data = bundle.getStringArrayList(speechRecognizer.RESULTS_RECOGNITION);
                            Log.e("SPEECH",data.get(0));
                            if(data.get(0).toUpperCase(Locale.ROOT).contains("CANCEL")){
                                textToSpeech.speak("HAVE A HEALTHY DAY", TextToSpeech.QUEUE_FLUSH, null, "denied");
                                (new Handler()).postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(PoseDetectStart.this,Home.class);
                                        intent.putExtra("phoneno",MainActivity.auto_phoneno);
                                        intent.putExtra("startingpage","1");
                                        startActivity(intent);
                                        finish();
                                    }
                                },2500);

                            }
                            else if(data.get(0).toUpperCase(Locale.ROOT).contains("YES")) {
                                textToSpeech.speak("CONFIRMING THE WORKOUT NAMED " + MainActivity.ref_angles.get(temp_error).getName(), TextToSpeech.QUEUE_FLUSH, null, "confirmation");
                                (new Handler()).postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(PoseDetectStart.this,Workout.class);
                                        intent.putExtra("AUTOWORKOUTINDEX",Integer.toString(temp_error));
                                        startActivity(intent);
                                        finish();
                                    }
                                },2500);
                            }
                            else{
                                textToSpeech.speak("WHICH WORKOUT YOU WANT TO DO ", TextToSpeech.QUEUE_FLUSH, null, "denied");
                                (new Handler()).postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        speechRecognizer.startListening(speechRecognizerIntent);
                                    }
                                },4000);
                                (new Handler()).postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        speechRecognizer.stopListening();
                                    }
                                },10000);
                                speechRecognizer.setRecognitionListener(new RecognitionListener() {
                                    @Override
                                    public void onReadyForSpeech(Bundle bundle) {

                                    }

                                    @Override
                                    public void onBeginningOfSpeech() {

                                    }

                                    @Override
                                    public void onRmsChanged(float v) {

                                    }

                                    @Override
                                    public void onBufferReceived(byte[] bytes) {

                                    }

                                    @Override
                                    public void onEndOfSpeech() {

                                    }

                                    @Override
                                    public void onError(int i) {

                                    }

                                    @Override
                                    public void onResults(Bundle bundle) {
                                        String res = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).get(0);
                                        res = res.replace(" ","");
                                        int chksum=0;
                                        Log.e("SPEECH",res);

                                        if(res.toUpperCase(Locale.ROOT).contains("CANCEL")){
                                            textToSpeech.speak("HAVE A HEALTHY DAY", TextToSpeech.QUEUE_FLUSH, null, "denied");
                                            (new Handler()).postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Intent intent = new Intent(PoseDetectStart.this,Home.class);
                                                    intent.putExtra("phoneno",MainActivity.auto_phoneno);
                                                    intent.putExtra("startingpage","1");
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            },2500);                                        }
                                        else{
                                            for(int i=0;i<MainActivity.ref_angles.size();i++){
                                                if(res.toUpperCase(Locale.ROOT).contains(MainActivity.ref_angles.get(i).getName().toUpperCase(Locale.ROOT)))
                                                {
                                                    Intent intent = new Intent(PoseDetectStart.this,Workout.class);
                                                    intent.putExtra("AUTOWORKOUTINDEX",Integer.toString(i));
                                                    chksum=1;
                                                    startActivity(intent);
                                                    finish();
                                                    break;
                                                }
                                            }
                                            if(chksum==0){
                                                textToSpeech.speak("HAVE A HEALTHY DAY", TextToSpeech.QUEUE_FLUSH, null, "denied");
                                                (new Handler()).postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Intent intent = new Intent(PoseDetectStart.this,Home.class);
                                                        intent.putExtra("phoneno",MainActivity.auto_phoneno);
                                                        intent.putExtra("startingpage","1");
                                                        startActivity(intent);

                                                        finish();
                                                    }
                                                },2500);}
                                        }
                                    }

                                    @Override
                                    public void onPartialResults(Bundle bundle) {
                                        Log.e("SPEECH","PARTIAL");

                                    }

                                    @Override
                                    public void onEvent(int i, Bundle bundle) {

                                    }
                                });

                            }

                        }

                        @Override
                        public void onPartialResults(Bundle bundle) {
                        }

                        @Override
                        public void onEvent(int i, Bundle bundle) {

                        }
                    });
                    handler.removeCallbacks(this);
                }

                Log.d("VALUE",Integer.toString(PoseDetectorProcessor.angle_errors)+ "  " + Integer.toString(temp_error)+ "  " +Integer.toString(c));
                if(c<5)handler.postDelayed(this,1000);
                else handler.removeCallbacks(this);
            }

        };
        handler.postDelayed(runnable,1000);

        Handler handler3=new Handler();
        Runnable runnable3 = new Runnable() {
            int timerr=30;
            @Override
            public void run() {
                if(stopthread==1){
                    handler3.removeCallbacks(this);
                    return;
                }
                handler3.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(stopthread==1){
                            handler3.removeCallbacks(this);
                            return;
                        }
                        if(autodetectworkout.toUpperCase(Locale.ROOT).equals("FALSE")){

                            if(!textToSpeech.isSpeaking()) {
                                if(workout_started_check_1!=1 && PoseDetectorProcessor.workout_started_check==1) {

                                    if (!PoseDetectStart.textToSpeech.isSpeaking() && workout_started_check_1!=1 && before_start_timer>=0) {
                                        before_start_timer--;
                                        workout_start_countdown.setVisibility(View.VISIBLE);
                                        workout_start_countdown.setText(Integer.toString(before_start_timer+1));
                                        textToSpeech.speak(Integer.toString(before_start_timer+1), TextToSpeech.QUEUE_FLUSH, null);
                                        Log.e("C",Integer.toString(before_start_timer));
                                    }

                                    if (!textToSpeech.isSpeaking() && before_start_timer <= 0) {
                                        workout_started_check_1 = 1;
                                        if(before_start_timer<=-1)workout_start_countdown.setVisibility(View.GONE);
                                    }
                                }
                            }
                        }

                        handler3.postDelayed(this,1000);
                    }
                },1000);
            }
        };
        thread1 = new Thread(runnable3);
        thread1.start();
        Handler handler2=new Handler();
        Runnable runnable2 = new Runnable() {
            int timerr=30;
            @Override
            public void run() {
                if(stopthread==1){
                    handler2.removeCallbacks(this);
                    return;
                }
                handler2.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(stopthread==1){
                            handler2.removeCallbacks(this);
                            return;
                        }
                        if(autodetectworkout.toUpperCase(Locale.ROOT).equals("FALSE")){

                            poseresult.setTextColor(Color.WHITE);
                            if(workout_started_check_1==1 && PoseDetectorProcessor.workout_started_check==1 && timerr>=0) {
                                        timerr--;
                                        Log.e("WORKOUT ACCURACY",Double.toString(PoseDetectorProcessor.workout_Accuracy*100)+"   "+Double.toString(PoseDetectorProcessor.workout_Accuracy_1));
                                        Double accuracy = 100- (PoseDetectorProcessor.workout_Accuracy*100)/PoseDetectorProcessor.workout_Accuracy_1;
                                        if(accuracy<0)accuracy=0.0;
                                        if(accuracy<40 && !textToSpeech.isSpeaking() && incorrect_workout_warning>0){
                                            textToSpeech.speak("INCORRECT POSTURE ! TRY TO DO IT BETTER", TextToSpeech.QUEUE_FLUSH, null, "denied");
                                            incorrect_workout_warning--;
                                        }
                                        if(accuracy>70){
                                            correct_workout_appreciate++;
                                        }
                                        if(correct_workout_appreciate>10 && !textToSpeech.isSpeaking()){
                                            textToSpeech.speak("CORRECT POSTURE ! GREAT GOING", TextToSpeech.QUEUE_FLUSH, null, "denied");
                                            correct_workout_appreciate=0;
                                        }

                                workout_start_timer.setVisibility(View.VISIBLE);
                                        workout_start_timer.setText(Integer.toString(timerr));
                                        poseresult.setText(Double.toString(Math.ceil(accuracy)));
                                        PoseDetectorProcessor.workout_Accuracy=0.0;
                                        PoseDetectorProcessor.workout_Accuracy_1=0;
                            }
                            if(timerr<0){
                                workout_start_timer.setVisibility(View.GONE);
                                textToSpeech.speak("WORKOUT FINISHED! HAVE A HEALTHY DAY", TextToSpeech.QUEUE_FLUSH, null);
                                stopthread=1;
                                Intent intent = new Intent(PoseDetectStart.this,Home.class);
                                intent.putExtra("phoneno",MainActivity.auto_phoneno);
                                intent.putExtra("startingpage","1");
                               /* DatabaseReference reference = FirebaseDatabase.getInstance("https://miniprojectyogify-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("users").child(MainActivity.auto_phoneno).child("data");

                                reference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(snapshot.exists()){
                                            reference.setValue("WORKOUT");
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });*/
                                startActivity(intent);
                                finish();

                            }
                        }
                        handler2.postDelayed(this,1000);
                    }
                },1000);
            }
        };
        thread = new Thread(runnable2);
        thread.start();


    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (cameraSource != null) {
            if (isChecked) {
                MainActivity.CameraFacing=0;
                cameraSource.setFacing(MainActivity.CameraFacing);
            } else {
                MainActivity.CameraFacing=1;
                cameraSource.setFacing(MainActivity.CameraFacing);
            }
        }
        preview.stop();
        startCameraSource();
    }

    private void createCameraSource(String model) {
        // If there's no existing cameraSource, create one.
        if (cameraSource == null) {
            cameraSource = new CameraSource(this, graphicOverlay);
            cameraSource.setFacing(MainActivity.CameraFacing);
        }

        try {
            PoseDetectorOptionsBase poseDetectorOptions =
                    PreferenceUtils.getPoseDetectorOptionsForLivePreview(this);
            Log.i("pose detector", "Using Pose Detector with options " + poseDetectorOptions);
            boolean shouldShowInFrameLikelihood =
                    PreferenceUtils.shouldShowPoseDetectionInFrameLikelihoodLivePreview(this);
            boolean visualizeZ = PreferenceUtils.shouldPoseDetectionVisualizeZ(this);
            boolean rescaleZ = PreferenceUtils.shouldPoseDetectionRescaleZForVisualization(this);
            boolean runClassification = PreferenceUtils.shouldPoseDetectionRunClassification(this);
            cameraSource.setMachineLearningFrameProcessor(new PoseDetectorProcessor(this, poseDetectorOptions, shouldShowInFrameLikelihood, visualizeZ, rescaleZ, runClassification,/* isStreamMode = */ true,MainActivity.ref_angles,autodetectworkout,workoutindex));

        } catch (RuntimeException e) {
            Toast.makeText(
                    getApplicationContext(),
                    "Can not create image processor: " + e.getMessage(),
                    Toast.LENGTH_LONG)
                    .show();
        }
    }
    private void startCameraSource() {
        if (cameraSource != null) {
            try {
                if (preview == null) {
                    Log.d("PREVIEW ERROR", "resume: Preview is null");
                }
                Log.d("PREVIEW STATUS: ","STARTED");
                preview.start(cameraSource, graphicOverlay);
            } catch (IOException e) {
                cameraSource.release();
                cameraSource = null;
            }
        }
    }

    @Override
    public boolean isActivityTransitionRunning() {
        return super.isActivityTransitionRunning();
    }

    @Override
    public void onResume() {
        super.onResume();
        createCameraSource(selectedModel);
        Log.i("PREVIEW STATUS: ","STARTED");
        startCameraSource();
    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.i("PREVIEW STATUS: ","STOPPED");
        preview.stop();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("PREVIEW STATUS: ","DESTROYED");
        c=0;
        before_start_timer=5;
        incorrect_workout_warning=3;
        correct_workout_appreciate =0;
        workout_started_check_1 =0;
        PoseDetectorProcessor.workout_started_check=0;
        stopthread=1;
        if (cameraSource != null) {
            cameraSource.release();
        }
    }
}