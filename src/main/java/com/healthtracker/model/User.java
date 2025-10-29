package com.healthtracker.model;

import java.math.BigDecimal;
import java.io.Serializable;

public class User implements Serializable {

    private int userId;
    private String name;
    private int heightCm;
    private BigDecimal startWeightKg;
    private BigDecimal targetWeightKg;

    public User(int userId, String name, int heightCm, BigDecimal startWeightKg, BigDecimal targetWeightKg) {
        this.userId = userId;
        this.name = name;
        this.heightCm = heightCm;
        this.startWeightKg = startWeightKg;
        this.targetWeightKg = targetWeightKg;
    }

    public User(String name, int heightCm, BigDecimal startWeightKg, BigDecimal targetWeightKg) {
        this(-1, name, heightCm, startWeightKg, targetWeightKg);
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

    @Override
    public String toString() {
        return "User [ID=" + userId + ", Name='" + name + "', Height=" + heightCm +
                ", StartWeight=" + startWeightKg + ", TargetWeight=" + targetWeightKg + "]";
    }
}