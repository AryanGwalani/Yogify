package com.example.miniproject_yogify;

public class workout_data {
    private String name;
    private double time;

    public workout_data(String name, double time) {
        this.name = name;
        this.time = time;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }
}
