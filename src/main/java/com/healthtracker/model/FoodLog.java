package com.healthtracker.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.io.Serializable;

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


    private int calories;
    private BigDecimal proteinG;
    private BigDecimal fatsG;
    private BigDecimal carbsG;


    public FoodLog(int foodId, int userId, Timestamp logDate, String description, String mealType,
                   int caloriesPer100g, BigDecimal proteinPer100g, BigDecimal fatsPer100g,
                   BigDecimal carbsPer100g, int portionSizeGrams, int calories,
                   BigDecimal proteinG, BigDecimal fatsG, BigDecimal carbsG) {
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
        this.calories = calories;
        this.proteinG = proteinG;
        this.fatsG = fatsG;
        this.carbsG = carbsG;
    }


    public FoodLog(int userId, String description, String mealType,
                   int caloriesPer100g, BigDecimal proteinPer100g, BigDecimal fatsPer100g,
                   BigDecimal carbsPer100g, int portionSizeGrams) {
        this(-1, userId, new Timestamp(System.currentTimeMillis()), description, mealType,
                caloriesPer100g, proteinPer100g, fatsPer100g, carbsPer100g, portionSizeGrams, 0, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);


        this.calories = calculateTotalCalories();
        this.proteinG = calculateTotalProtein();
        this.fatsG = calculateTotalFats();
        this.carbsG = calculateTotalCarbs();
    }


    public int calculateTotalCalories() {
        return (int) Math.round(((double) portionSizeGrams / 100.0) * caloriesPer100g);
    }

    public BigDecimal calculateTotalProtein() {
        return proteinPer100g.multiply(new BigDecimal(portionSizeGrams).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP));
    }

    public BigDecimal calculateTotalFats() {
        return fatsPer100g.multiply(new BigDecimal(portionSizeGrams).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP));
    }

    public BigDecimal calculateTotalCarbs() {
        return carbsPer100g.multiply(new BigDecimal(portionSizeGrams).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP));
    }

    public int getFoodId() { return foodId; }
    public void setFoodId(int foodId) { this.foodId = foodId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public Timestamp getLogDate() { return logDate; }
    public void setLogDate(Timestamp logDate) { this.logDate = logDate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getMealType() { return mealType; }
    public void setMealType(String mealType) { this.mealType = mealType; }

    public int getCaloriesPer100g() { return caloriesPer100g; }
    public void setCaloriesPer100g(int caloriesPer100g) { this.caloriesPer100g = caloriesPer100g; }

    public BigDecimal getProteinPer100g() { return proteinPer100g; }
    public void setProteinPer100g(BigDecimal proteinPer100g) { this.proteinPer100g = proteinPer100g; }

    public BigDecimal getFatsPer100g() { return fatsPer100g; }
    public void setFatsPer100g(BigDecimal fatsPer100g) { this.fatsPer100g = fatsPer100g; }

    public BigDecimal getCarbsPer100g() { return carbsPer100g; }
    public void setCarbsPer100g(BigDecimal carbsPer100g) { this.carbsPer100g = carbsPer100g; }

    public int getPortionSizeGrams() { return portionSizeGrams; }
    public void setPortionSizeGrams(int portionSizeGrams) { this.portionSizeGrams = portionSizeGrams; }

    public int getCalories() { return calories; }
    public void setCalories(int calories) { this.calories = calories; }

    public BigDecimal getProteinG() { return proteinG; }
    public void setProteinG(BigDecimal proteinG) { this.proteinG = proteinG; }

    public BigDecimal getFatsG() { return fatsG; }
    public void setFatsG(BigDecimal fatsG) { this.fatsG = fatsG; }

    public BigDecimal getCarbsG() { return carbsG; }
    public void setCarbsG(BigDecimal carbsG) { this.carbsG = carbsG; }
}