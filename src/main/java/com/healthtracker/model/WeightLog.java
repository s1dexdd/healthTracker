package com.healthtracker.model;

import java.math.BigDecimal;
import java.sql.Date;
import java.io.Serializable;

public class WeightLog implements Serializable {
    private int logId;
    private int userId;
    private Date logDate;
    private BigDecimal currentWeightKg;

    public WeightLog(int logId, int userId, Date logDate, BigDecimal currentWeightKg) {
        this.logId = logId;
        this.userId = userId;
        this.logDate = logDate;
        this.currentWeightKg = currentWeightKg;
    }
    public WeightLog(int userId,Date logDate, BigDecimal currentWeightKg){
        this(-1, userId, logDate, currentWeightKg);
    }

    public int getLogId() {
        return logId;
    }

    public void setLogId(int logId) {
        this.logId = logId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Date getLogDate() {
        return logDate;
    }

    public void setLogDate(Date logDate) {
        this.logDate = logDate;
    }

    public BigDecimal getCurrentWeightKg() {
        return currentWeightKg;
    }

    public void setCurrentWeightKg(BigDecimal currentWeightKg) {
        this.currentWeightKg = currentWeightKg;
    }

    @Override
    public String toString() {
        return "WeightLog [ID=" + logId + ", userID=" + userId + ", logDate=" + logDate + ", weight=" + currentWeightKg+ "kg" + "]";
    }

}