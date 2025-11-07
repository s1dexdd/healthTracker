package com.healthtracker.model;

import com.healthtracker.model.WeightLog;
import java.math.BigDecimal;
import java.io.Serializable;

public class User implements Serializable {

    private int userId;
    private String name;
    private int heightCm;
    private BigDecimal startWeightKg;
    private BigDecimal targetWeightKg;
    private int age;
    public enum Gender{
        MALE,
        FEMALE
    }
    public enum ActivityLevel{
        SIT(1.2f),
        LIGHT(1.375f),
        MID(1.55f);

        private final float coefficient;

        ActivityLevel(float coefficient) {
            this.coefficient = coefficient;
        }
        public float getCoefficient() {
            return coefficient;
        }
    }

    private Gender gender;
    private ActivityLevel activityLevel;



    public User(int userId, String name, int heightCm, BigDecimal startWeightKg, BigDecimal targetWeightKg,int age, Gender gender, ActivityLevel activityLevel) {
        this.userId = userId;
        this.name = name;
        this.heightCm = heightCm;
        this.startWeightKg = startWeightKg;
        this.targetWeightKg = targetWeightKg;
        this.age= age;
        this.gender=gender;
        this.activityLevel=activityLevel;
    }

    public User(String name, int heightCm, BigDecimal startWeightKg, BigDecimal targetWeightKg, int age,Gender gender,ActivityLevel activityLevel) {
        this(-1, name, heightCm, startWeightKg, targetWeightKg, age, gender, activityLevel);
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getHeightCm() {
        return heightCm;
    }

    public void setHeightCm(int heightCm) {
        this.heightCm = heightCm;
    }

    public BigDecimal getStartWeightKg() {
        return startWeightKg;
    }

    public void setStartWeightKg(BigDecimal startWeightKg) {
        this.startWeightKg = startWeightKg;
    }

    public BigDecimal getTargetWeightKg() {
        return targetWeightKg;
    }

    public void setTargetWeightKg(BigDecimal targetWeightKg) {
        this.targetWeightKg = targetWeightKg;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public ActivityLevel getActivityLevel() {
        return activityLevel;
    }

    public void setActivityLevel(ActivityLevel activityLevel) {
        this.activityLevel = activityLevel;
    }



    @Override
    public String toString() {
        return "User [ID=" + userId + ", Name='" + name + "', Height=" + heightCm +
                ", StartWeight=" + startWeightKg + ", TargetWeight=" + targetWeightKg + ", Age=" + age + ", Gender=" + gender + ", Activity level: " + activityLevel+ "]";
    }
}