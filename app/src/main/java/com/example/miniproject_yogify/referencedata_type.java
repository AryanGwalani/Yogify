package com.example.miniproject_yogify;

public class referencedata_type {
    private String name;
    private Integer left_elbow_angle;
    private Integer left_knee_angle;
    private Integer left_shoulder_angle;
    private Integer right_elbow_angle;
    private Integer right_knee_angle;
    private Integer right_shoulder_angle;

    public referencedata_type(String name, Integer left_elbow_angle, Integer left_knee_angle, Integer left_shoulder_angle, Integer right_elbow_angle, Integer right_knee_angle, Integer right_shoulder_angle) {
        this.name = name;
        this.left_elbow_angle = left_elbow_angle;
        this.left_knee_angle = left_knee_angle;
        this.left_shoulder_angle = left_shoulder_angle;
        this.right_elbow_angle = right_elbow_angle;
        this.right_knee_angle = right_knee_angle;
        this.right_shoulder_angle = right_shoulder_angle;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getLeft_elbow_angle() {
        return left_elbow_angle;
    }

    public void setLeft_elbow_angle(Integer left_elbow_angle) {
        this.left_elbow_angle = left_elbow_angle;
    }

    public Integer getLeft_knee_angle() {
        return left_knee_angle;
    }

    public void setLeft_knee_angle(Integer left_knee_angle) {
        this.left_knee_angle = left_knee_angle;
    }

    public Integer getLeft_shoulder_angle() {
        return left_shoulder_angle;
    }

    public void setLeft_shoulder_angle(Integer left_shoulder_angle) {
        this.left_shoulder_angle = left_shoulder_angle;
    }

    public Integer getRight_elbow_angle() {
        return right_elbow_angle;
    }

    public void setRight_elbow_angle(Integer right_elbow_angle) {
        this.right_elbow_angle = right_elbow_angle;
    }

    public Integer getRight_knee_angle() {
        return right_knee_angle;
    }

    public void setRight_knee_angle(Integer right_knee_angle) {
        this.right_knee_angle = right_knee_angle;
    }

    public Integer getRight_shoulder_angle() {
        return right_shoulder_angle;
    }

    public void setRight_shoulder_angle(Integer right_shoulder_angle) {
        this.right_shoulder_angle = right_shoulder_angle;
    }
}
