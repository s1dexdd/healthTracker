package com.healthtracker.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.io.Serializable;
import java.time.LocalDateTime;

public class FoodLog implements Serializable {

    private int foodId;
    private int userId;
    private Timestamp logDate;
    private String description;
    private String mealType;


    private int caloriesPer100g;
    private BigDecimal proteinPer100g;
    private BigDecimal fatsPer100g;
    private BigDecimal carbsPer100g;

    private int portionSizeGrams;


    public FoodLog(int foodId, int userId, Timestamp logDate, String description, String mealType, int caloriesPer100g,
                   BigDecimal proteinPer100g, BigDecimal fatsPer100g, BigDecimal carbsPer100g, int portionSizeGrams) {
        this.foodId = foodId;
        this.userId = userId;
        this.logDate = logDate;
        this.description = description;
        this.mealType = mealType;
        this.caloriesPer100g = caloriesPer100g;
        this.proteinPer100g = proteinPer100g;
        this.fatsPer100g = fatsPer100g;
        this.carbsPer100g = carbsPer100g;
        this.portionSizeGrams = portionSizeGrams;
    }


    public FoodLog(int userId, String description, String mealType, int caloriesPer100g,
                   BigDecimal proteinPer100g, BigDecimal fatsPer100g, BigDecimal carbsPer100g, int portionSizeGrams) {

        this(-1, userId, Timestamp.valueOf(LocalDateTime.now()), description, mealType,
                caloriesPer100g, proteinPer100g, fatsPer100g, carbsPer100g, portionSizeGrams);
    }



    public int calculateTotalCalories() {
        if (portionSizeGrams <= 0) return 0;
        BigDecimal total = new BigDecimal(caloriesPer100g)
                .multiply(new BigDecimal(portionSizeGrams))
                .divide(new BigDecimal(100), 0, RoundingMode.HALF_UP);
        return total.intValue();
    }

    private BigDecimal calculateMacro(BigDecimal macroPer100g) {
        if (portionSizeGrams <= 0 || macroPer100g == null) return BigDecimal.ZERO;
        return macroPer100g.multiply(new BigDecimal(portionSizeGrams))
                .divide(new BigDecimal(100), 1, RoundingMode.HALF_UP);
    }

    public BigDecimal getTotalProteinG() { return calculateMacro(proteinPer100g); }
    public BigDecimal getTotalFatsG() { return calculateMacro(fatsPer100g); }
    public BigDecimal getTotalCarbsG() { return calculateMacro(carbsPer100g); }


    public Timestamp getLogDate() { return logDate; }
    public int getCaloriesPer100g() { return caloriesPer100g; }
    public int getPortionSizeGrams() { return portionSizeGrams; }
    public String getDescription() { return description; }
    public int getUserId() { return userId; }
    public String getMealType() { return mealType; }
    public BigDecimal getProteinPer100g() { return proteinPer100g; }
    public BigDecimal getFatsPer100g() { return fatsPer100g; }
    public BigDecimal getCarbsPer100g() { return carbsPer100g; }
}
