package com.healthtracker.model;


public class GoalResult {
    public static final int KCAL_PER_PROTEIN_G = 4;
    public static final int KCAL_PER_CARBS_G = 4;
    public static final int KCAL_PER_FATS_G = 9;
    private final int targetIntakeKcal;
    private final int dailyDeficitOrSurplusKcal;
    private final String goalDescription;
    private final float totalWeightChangeKg;
    private final int proteinGrams;
    private final int fatsGrams;
    private final int carbsGrams;

    public GoalResult(int targetIntakeKcal, int dailyDeficitOrSurplusKcal, String goalDescription, float totalWeightChangeKg, int proteinGrams, int fatsGrams, int carbsGrams) {
        this.targetIntakeKcal = targetIntakeKcal;
        this.dailyDeficitOrSurplusKcal = dailyDeficitOrSurplusKcal;
        this.goalDescription = goalDescription;
        this.totalWeightChangeKg = totalWeightChangeKg;
        this.proteinGrams = proteinGrams;
        this.fatsGrams = fatsGrams;
        this.carbsGrams = carbsGrams;
    }

    public int getTargetIntakeKcal() {
        return targetIntakeKcal;
    }

    public int getDailyDeficitOrSurplusKcal() {
        return dailyDeficitOrSurplusKcal;
    }

    public String getGoalDesccription() {
        return goalDescription;
    }

    public float getTotalWeightChangeKg() {
        return totalWeightChangeKg;
    }

    public int getProteinGrams() {
        return proteinGrams;
    }

    public int getFatsGrams() {
        return fatsGrams;
    }

    public int getCarbsGrams() {
        return carbsGrams;
    }

    @Override
    public String toString() {
        String sign = dailyDeficitOrSurplusKcal >= 0 ? "Профицит" : "Дефицит";
        return "GoalResult {" +
                "Целевое потребление: " + targetIntakeKcal + " ккал/день, " +
                sign + ": " + Math.abs(dailyDeficitOrSurplusKcal) + " ккал/день, " +
                "Цель: " + goalDescription + ", " +
                "Протеин: " + proteinGrams + "г, " +
                "Жиры: " + fatsGrams + "г, " +
                "Углеводы: " + carbsGrams + "г" +
                '}';
    }
}