/*
 * Copyright 2020 Google LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.miniproject_yogify.posedetector;

import android.content.Context;
import android.graphics.Color;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.miniproject_yogify.MainActivity;
import com.example.miniproject_yogify.PoseDetectStart;
import com.example.miniproject_yogify.referencedata_type;
import com.google.android.gms.tasks.Task;
import com.google.android.odml.image.MlImage;
import com.google.common.base.Stopwatch;
import com.google.mlkit.vision.common.InputImage;
import com.example.miniproject_yogify.GraphicOverlay;
import com.example.miniproject_yogify.VisionProcessorBase;
import com.example.miniproject_yogify.posedetector.classification.PoseClassifierProcessor;
import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseDetection;
import com.google.mlkit.vision.pose.PoseDetector;
import com.google.mlkit.vision.pose.PoseDetectorOptionsBase;
import com.google.mlkit.vision.pose.PoseLandmark;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import android.os.Handler;
import android.widget.Toast;

/** A processor to run pose detector. */
public class PoseDetectorProcessor
    extends VisionProcessorBase<PoseDetectorProcessor.PoseWithClassification> {
  private static final String TAG = "PoseDetectorProcessor";

  private final PoseDetector detector;

  private final boolean showInFrameLikelihood;
  private final boolean visualizeZ;
  private final boolean rescaleZForVisualization;
  private final boolean runClassification;
  private final boolean isStreamMode;
  private final Context context;
  private final Executor classificationExecutor;
  private ArrayList<referencedata_type> ref_angle;
  private String autodetectworkout,workoutindex;

  double classified_pose_index=-1;
  public static int angle_errors=-1;
  public static Double workout_Accuracy =0.0;
  public static int workout_Accuracy_1 =0;
  public static int workout_started_check =0;
  int workout_started_check_1 =0;
  int c = 5;
  int timerr = 30;



  public static double img_width,img_height;
  private PoseClassifierProcessor poseClassifierProcessor;
  /** Internal class to hold Pose and classification results. */
  protected static class PoseWithClassification {
    private final Pose pose;
    private final List<String> classificationResult;

    public PoseWithClassification(Pose pose, List<String> classificationResult) {
      this.pose = pose;
      this.classificationResult = classificationResult;
    }

    public Pose getPose() {
      return pose;
    }

    public List<String> getClassificationResult() {
      return classificationResult;
    }
  }

  public PoseDetectorProcessor(
          Context context,
          PoseDetectorOptionsBase options,
          boolean showInFrameLikelihood,
          boolean visualizeZ,
          boolean rescaleZForVisualization,
          boolean runClassification,
          boolean isStreamMode,
          ArrayList<referencedata_type> ref_angles,
          String autodetectworkout,
          String workoutindex) {
    super(context);
    this.showInFrameLikelihood = showInFrameLikelihood;
    this.visualizeZ = visualizeZ;
    this.rescaleZForVisualization = rescaleZForVisualization;
    detector = PoseDetection.getClient(options);
    this.runClassification = runClassification;
    this.isStreamMode = isStreamMode;
    this.context = context;
    classificationExecutor = Executors.newSingleThreadExecutor();
    this.ref_angle = ref_angles;
    this.autodetectworkout= autodetectworkout;
    this.workoutindex = workoutindex;
  }

  @Override
  public void stop() {
    super.stop();
    detector.close();
  }

  @Override
  protected Task<PoseWithClassification> detectInImage(InputImage image) {
    return detector
        .process(image)
        .continueWith(
            classificationExecutor,
            task -> {
              Pose pose = task.getResult();
              List<String> classificationResult = new ArrayList<>();
              if (runClassification) {
                if (poseClassifierProcessor == null) {
                  poseClassifierProcessor = new PoseClassifierProcessor(context, isStreamMode);
                }
                classificationResult = poseClassifierProcessor.getPoseResult(pose);
              }
              return new PoseWithClassification(pose, classificationResult);
            });
  }

  @Override
  protected Task<PoseWithClassification> detectInImage(MlImage image) {
    return detector
        .process(image)
        .continueWith(
            classificationExecutor,
            task -> {
              Pose pose = task.getResult();
              List<String> classificationResult = new ArrayList<>();
              img_height=image.getHeight();
              img_width = image.getWidth();
              if (runClassification) {
                if (poseClassifierProcessor == null) {
                  poseClassifierProcessor = new PoseClassifierProcessor(context, isStreamMode);
                }
                classificationResult = poseClassifierProcessor.getPoseResult(pose);
              }
              return new PoseWithClassification(pose, classificationResult);
            });
  }

  @Override
  protected void onSuccess(
      @NonNull PoseWithClassification poseWithClassification,
      @NonNull GraphicOverlay graphicOverlay) {

    try {
      graphicOverlay.add(
              new PoseGraphic(
                      graphicOverlay,
                      poseWithClassification.pose,
                      showInFrameLikelihood,
                      visualizeZ,
                      rescaleZForVisualization,
                      poseWithClassification.classificationResult));
        int err = 0;
        classified_pose_index=-1;
        List<PoseLandmark> landmarks = poseWithClassification.pose.getAllPoseLandmarks();
        for (PoseLandmark landmark : landmarks) {
          if (landmark.getInFrameLikelihood() < 0.95) {
            err = 1;
            classified_pose_index=-1;
            angle_errors=-1;
            break;
          }
        }
        if (err == 0) angle_errors = comparePose(poseWithClassification.pose, ref_angle);


    }
    catch (Exception e){
    }
  }

  @Override
  protected void onFailure(@NonNull Exception e) {
    Log.e(TAG, "Pose detection failed!", e);
  }

  @Override
  protected boolean isMlImageEnabled(Context context) {
    // Use MlImage in Pose Detection by default, change it to OFF to switch to InputImage.
    return true;
  }

  public int comparePose(Pose pose,ArrayList<referencedata_type> ref_angle){
    double angle_errors[][],errorchk;
    classified_pose_index=-1;


//ANGLE CALCULATING
    double angle1 = calculateAngle(
            pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER).getPosition3D().getX(),
            pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER).getPosition3D().getY(),
            pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW).getPosition3D().getX(),
            pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW).getPosition3D().getY(),
            pose.getPoseLandmark(PoseLandmark.LEFT_WRIST).getPosition3D().getX(),
            pose.getPoseLandmark(PoseLandmark.LEFT_WRIST).getPosition3D().getY());

    double angle2 = calculateAngle(
            pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER).getPosition3D().getX(),
            pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER).getPosition3D().getY(),
            pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW).getPosition3D().getX(),
            pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW).getPosition3D().getY(),
            pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST).getPosition3D().getX(),
            pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST).getPosition3D().getY());

    double angle3 = calculateAngle(
            pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW).getPosition3D().getX(),
            pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW).getPosition3D().getY(),
            pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER).getPosition3D().getX(),
            pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER).getPosition3D().getY(),
            pose.getPoseLandmark(PoseLandmark.LEFT_HIP).getPosition3D().getX(),
            pose.getPoseLandmark(PoseLandmark.LEFT_HIP).getPosition3D().getY());
    double angle4 = calculateAngle(
            pose.getPoseLandmark(24).getPosition3D().getX(),
            pose.getPoseLandmark(24).getPosition3D().getY(),
            pose.getPoseLandmark(12).getPosition3D().getX(),
            pose.getPoseLandmark(12).getPosition3D().getY(),
            pose.getPoseLandmark(14).getPosition3D().getX(),
            pose.getPoseLandmark(14).getPosition3D().getY());

    double angle5 = calculateAngle(
            pose.getPoseLandmark(23).getPosition3D().getX(),
            pose.getPoseLandmark(23).getPosition3D().getY(),
            pose.getPoseLandmark(25).getPosition3D().getX(),
            pose.getPoseLandmark(25).getPosition3D().getY(),
            pose.getPoseLandmark(27).getPosition3D().getX(),
            pose.getPoseLandmark(27).getPosition3D().getY());
    double angle6 = calculateAngle(
            pose.getPoseLandmark(24).getPosition3D().getX(),
            pose.getPoseLandmark(24).getPosition3D().getY(),
            pose.getPoseLandmark(26).getPosition3D().getX(),
            pose.getPoseLandmark(26).getPosition3D().getY(),
            pose.getPoseLandmark(28).getPosition3D().getX(),
            pose.getPoseLandmark(28).getPosition3D().getY());
    Log.i("Left Elbow Angle",Double.toString(angle1));
    Log.i("Right Elbow Angle",Double.toString(angle2));
    Log.i("Left Shoulder Angle",Double.toString(angle3));
    Log.i("Right Shoulder Angle",Double.toString(angle4));
    Log.i("Left Knee Angle",Double.toString(angle5));
    Log.i("Right Knee Angle",Double.toString(angle6));
    Log.i("FINAL POSE", PoseDetectStart.poseresult.getText().toString());
    Log.i("---NEXT-----","----------DATA------------");
    if(autodetectworkout.toUpperCase(Locale.ROOT).equals("TRUE")) {
      double total_error, min_error = 0;
      angle_errors = new double[ref_angle.size()][6];
      for (int i = 0; i < ref_angle.size(); i++) {
        total_error = 0;
        errorchk = Math.abs(Math.max(angle1, 180 - angle1) - Math.max(ref_angle.get(i).getLeft_elbow_angle(), 180 - ref_angle.get(i).getLeft_elbow_angle())) / Math.max(ref_angle.get(i).getLeft_elbow_angle(), 180 - ref_angle.get(i).getLeft_elbow_angle());
        angle_errors[i][0] = errorchk;
        total_error += errorchk;
        errorchk = Math.abs(Math.max(angle2, 180 - angle2) - Math.max(ref_angle.get(i).getRight_elbow_angle(), 180 - ref_angle.get(i).getRight_elbow_angle())) / Math.max(ref_angle.get(i).getRight_elbow_angle(), 180 - ref_angle.get(i).getRight_elbow_angle());
        angle_errors[i][1] = errorchk;
        total_error += errorchk;
        errorchk = Math.abs(Math.max(angle3, 180 - angle3) - Math.max(ref_angle.get(i).getLeft_shoulder_angle(), 180 - ref_angle.get(i).getLeft_shoulder_angle())) / Math.max(ref_angle.get(i).getLeft_shoulder_angle(), 180 - ref_angle.get(i).getLeft_shoulder_angle());
        angle_errors[i][2] = errorchk;
        total_error += errorchk;
        errorchk = Math.abs(Math.max(angle4, 180 - angle4) - Math.max(ref_angle.get(i).getRight_shoulder_angle(), 180 - ref_angle.get(i).getRight_shoulder_angle())) / Math.max(ref_angle.get(i).getRight_shoulder_angle(), 180 - ref_angle.get(i).getRight_shoulder_angle());
        angle_errors[i][3] = errorchk;
        total_error += errorchk;
        errorchk = Math.abs(Math.max(angle5, 180 - angle5) - Math.max(ref_angle.get(i).getLeft_knee_angle(), 180 - ref_angle.get(i).getLeft_knee_angle())) / Math.max(ref_angle.get(i).getLeft_knee_angle(), 180 - ref_angle.get(i).getLeft_knee_angle());
        angle_errors[i][4] = errorchk;
        total_error += errorchk;
        errorchk = Math.abs(Math.max(angle6, 180 - angle6) - Math.max(ref_angle.get(i).getRight_knee_angle(), 180 - ref_angle.get(i).getRight_knee_angle())) / Math.max(ref_angle.get(i).getRight_knee_angle(), 180 - ref_angle.get(i).getRight_knee_angle());
        angle_errors[i][5] = errorchk;
        total_error += errorchk;

        Log.i("TOTAL ERROR " + Integer.toString(i), Double.toString(total_error * 100) + "  ERRORS " + Double.toString(angle_errors[i][0] * 100) + "  "
                + Double.toString(angle_errors[i][1] * 100) + "  " + Double.toString(angle_errors[i][2] * 100) + "  "
                + Double.toString(angle_errors[i][3] * 100) + "  " + Double.toString(angle_errors[i][4] * 100) + "  " + Double.toString(angle_errors[i][5] * 100));
        if ((total_error * 100) <= 60) {
          if ((total_error < min_error) | (min_error == 0)) {
            min_error = total_error;
            classified_pose_index = i;
          }
        }
      }

      if (classified_pose_index >= 0 && (min_error * 100) <= 60) {
        PoseDetectStart.poseresult.setTextColor(Color.GREEN);
        PoseDetectStart.poseresult.setText(ref_angle.get((int) classified_pose_index).getName());
      } else {
        classified_pose_index = -1;
        PoseDetectStart.poseresult.setTextColor(Color.RED);
        PoseDetectStart.poseresult.setText("UNKNOWN POSE");
      }
    }
    if(autodetectworkout.toUpperCase(Locale.ROOT).equals("FALSE")){

      Handler handler = new Handler();
      if(!PoseDetectStart.textToSpeech.isSpeaking()) {
        if(workout_started_check!=1){
          PoseDetectStart.textToSpeech.speak("Workout Starting in", TextToSpeech.QUEUE_FLUSH,null);
          workout_started_check = 1;
        }
      }

      float total_error = 0;
      //classified_pose_index=-1;

      errorchk = Math.abs(Math.max(angle1, 180 - angle1) - Math.max(ref_angle.get(Integer.parseInt(workoutindex)).getLeft_elbow_angle(), 180 - ref_angle.get(Integer.parseInt(workoutindex)).getLeft_elbow_angle())) / Math.max(ref_angle.get(Integer.parseInt(workoutindex)).getLeft_elbow_angle(), 180 - ref_angle.get(Integer.parseInt(workoutindex)).getLeft_elbow_angle());
      total_error += errorchk;
      errorchk = Math.abs(Math.max(angle2, 180 - angle2) - Math.max(ref_angle.get(Integer.parseInt(workoutindex)).getRight_elbow_angle(), 180 - ref_angle.get(Integer.parseInt(workoutindex)).getRight_elbow_angle())) / Math.max(ref_angle.get(Integer.parseInt(workoutindex)).getRight_elbow_angle(), 180 - ref_angle.get(Integer.parseInt(workoutindex)).getRight_elbow_angle());
      total_error += errorchk;
      errorchk = Math.abs(Math.max(angle3, 180 - angle3) - Math.max(ref_angle.get(Integer.parseInt(workoutindex)).getLeft_shoulder_angle(), 180 - ref_angle.get(Integer.parseInt(workoutindex)).getLeft_shoulder_angle())) / Math.max(ref_angle.get(Integer.parseInt(workoutindex)).getLeft_shoulder_angle(), 180 - ref_angle.get(Integer.parseInt(workoutindex)).getLeft_shoulder_angle());
      total_error += errorchk;
      errorchk = Math.abs(Math.max(angle4, 180 - angle4) - Math.max(ref_angle.get(Integer.parseInt(workoutindex)).getRight_shoulder_angle(), 180 - ref_angle.get(Integer.parseInt(workoutindex)).getRight_shoulder_angle())) / Math.max(ref_angle.get(Integer.parseInt(workoutindex)).getRight_shoulder_angle(), 180 - ref_angle.get(Integer.parseInt(workoutindex)).getRight_shoulder_angle());
      total_error += errorchk;
      errorchk = Math.abs(Math.max(angle5, 180 - angle5) - Math.max(ref_angle.get(Integer.parseInt(workoutindex)).getLeft_knee_angle(), 180 - ref_angle.get(Integer.parseInt(workoutindex)).getLeft_knee_angle())) / Math.max(ref_angle.get(Integer.parseInt(workoutindex)).getLeft_knee_angle(), 180 - ref_angle.get(Integer.parseInt(workoutindex)).getLeft_knee_angle());
      total_error += errorchk;
      errorchk = Math.abs(Math.max(angle6, 180 - angle6) - Math.max(ref_angle.get(Integer.parseInt(workoutindex)).getRight_knee_angle(), 180 - ref_angle.get(Integer.parseInt(workoutindex)).getRight_knee_angle())) / Math.max(ref_angle.get(Integer.parseInt(workoutindex)).getRight_knee_angle(), 180 - ref_angle.get(Integer.parseInt(workoutindex)).getRight_knee_angle());
      total_error += errorchk;

      PoseDetectStart.poseresult.setTextColor(Color.WHITE);
      workout_Accuracy_1++;
      workout_Accuracy = workout_Accuracy+ total_error;
    }
    return (int)classified_pose_index;
  }


  public int calculateAngle(double landmark1_x,double landmark1_y, double landmark2_x,double landmark2_y, double landmark3_x,double landmark3_y) {
    landmark1_x=landmark1_x*img_width;
    landmark2_x=landmark2_x*img_width;
    landmark3_x=landmark3_x*img_width;
    landmark1_y=landmark1_y*img_height;
    landmark2_y=landmark2_y*img_height;
    landmark3_y=landmark3_y*img_height;
    double angle = Math.toDegrees(Math.atan2((landmark3_y-landmark2_y),(landmark3_x-landmark2_x)) - Math.atan2((landmark1_y-landmark2_y), (landmark1_x-landmark2_x)));
    angle = Math.abs(angle);
    if( angle > 180){
    angle = 360-angle;}

    return (int) Math.ceil(angle);
  }

}