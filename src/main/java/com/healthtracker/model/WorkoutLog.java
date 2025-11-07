package com.healthtracker.model;

import java.sql.Timestamp;
import java.io.Serializable;
import java.time.LocalDateTime;

public class WorkoutLog implements Serializable{
    private int workoutId;
    private int userId;
    private Timestamp logDate;
    private String type;
    private int durationMinutes;
    private int caloriesBurned;
    private int caloriesBurnedPerMinute;

    public WorkoutLog(int workoutId, int userId, Timestamp logDate, String type, int durationMinutes, int caloriesBurned, int caloriesBurnedPerMinute) {
        this.workoutId = workoutId;
        this.userId = userId;
        this.logDate = logDate;
        this.type = type;
        this.durationMinutes = durationMinutes;
        this.caloriesBurned = caloriesBurned;
        this.caloriesBurnedPerMinute = caloriesBurnedPerMinute;
    }
    private static int calculateBurned(int durationMinutes,int caloriesBurnedPerMinute) {
        if(durationMinutes<=0 || caloriesBurnedPerMinute<=0)return 0;
        return durationMinutes * caloriesBurnedPerMinute;
    }

    public WorkoutLog(int userId, String type, int durationMinutes, int caloriesBurnedPerMinute){
        this(-1,
                userId,
                Timestamp.valueOf(LocalDateTime.now()),
                type,
                durationMinutes,
                calculateBurned(durationMinutes,caloriesBurnedPerMinute),
                caloriesBurnedPerMinute );

    }
    public int calculateTotalBurned(){
        return this.caloriesBurned;
    }


    public int getWorkoutId() {
        return workoutId;
    }

    public void setWorkoutId(int workoutId) {
        this.workoutId = workoutId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Timestamp getLogDate() {
        return logDate;
    }

    public void setLogDate(Timestamp logDate) {
        this.logDate = logDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public int getCaloriesBurned() {
        return caloriesBurned;
    }

    public void setCaloriesBurned(int caloriesBurned) {
        this.caloriesBurned = caloriesBurned;
    }

    public int getCaloriesBurnedPerMinute() {
        return caloriesBurnedPerMinute;
    }

    public void setCaloriesBurnedPerMinute(int caloriesBurnedPerMinute) {
        this.caloriesBurnedPerMinute = caloriesBurnedPerMinute;
    }
}


