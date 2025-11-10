package com.healthtracker.service;

import com.healthtracker.dao.ReportDAO;
import com.healthtracker.dao.UserDAO;
import com.healthtracker.dao.WeightLogDAO;
import com.healthtracker.model.GoalResult;
import com.healthtracker.model.User;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class GoalCalculationService {
    private static final int CALORIES_PER_KG = 7700;
    private static final int DAYS_IN_WEEK = 7;

    private final ReportDAO reportDAO;
    private final WeightLogDAO weightLogDAO;
    private final UserDAO userDAO;

    public GoalCalculationService(ReportDAO reportDAO, WeightLogDAO weightLogDAO, UserDAO userDAO) {
        this.reportDAO = reportDAO;
        this.weightLogDAO = weightLogDAO;
        this.userDAO = userDAO;
    }

    public GoalResult calculateTargetIntakeByWeeklyRate(int userId, BigDecimal weeklyRateKg) {
        User user = userDAO.selectUser(userId);
        if (user == null) {
            throw new IllegalArgumentException("Пользователь не найден.");
        }

        int neat = reportDAO.calculateDailyNeatExpenditure(userId);

        BigDecimal totalKcalPerWeek = weeklyRateKg.multiply(new BigDecimal(CALORIES_PER_KG));

        int dailyDeficitOrSurplus = totalKcalPerWeek.divide(new BigDecimal(DAYS_IN_WEEK), 0, RoundingMode.HALF_UP)
                .intValue();

        int targetIntakeKcal = neat + dailyDeficitOrSurplus;


        if (user.getGender() == User.Gender.FEMALE && targetIntakeKcal < 1200) {
            targetIntakeKcal = 1200;
        } else if (user.getGender() == User.Gender.MALE && targetIntakeKcal < 1500) {
            targetIntakeKcal = 1500;
        }


        String goalDescription = "Поддержание веса";
        if (weeklyRateKg.floatValue() < 0) {
            goalDescription = "Похудение";
        } else if (weeklyRateKg.floatValue() > 0) {
            goalDescription = "Набор веса";
        }


        int[] macros = calculateTargetMacros(targetIntakeKcal);
        int proteinGrams = macros[0];
        int fatsGrams = macros[1];
        int carbsGrams = macros[2];


        float totalWeightChangeKg = weeklyRateKg.floatValue();

        return new GoalResult(
                targetIntakeKcal,
                dailyDeficitOrSurplus,
                goalDescription,
                totalWeightChangeKg,
                proteinGrams, // Протеин
                fatsGrams,    // Жиры
                carbsGrams    // Углеводы
        );
    }

    private int[] calculateTargetMacros(int targetIntakeKcal) {

        final double PROTEIN_PERCENT = 0.35;
        final double FATS_PERCENT = 0.25;

        final double CARBS_PERCENT = 1.00 - PROTEIN_PERCENT - FATS_PERCENT;


        int proteinKcal = (int) Math.round(targetIntakeKcal * PROTEIN_PERCENT);
        int fatsKcal = (int) Math.round(targetIntakeKcal * FATS_PERCENT);
        int carbsKcal = (int) Math.round(targetIntakeKcal * CARBS_PERCENT);

        int proteinGrams = proteinKcal / GoalResult.KCAL_PER_PROTEIN_G;
        int fatsGrams = fatsKcal / GoalResult.KCAL_PER_FATS_G;
        int carbsGrams = carbsKcal / GoalResult.KCAL_PER_CARBS_G;


        return new int[]{proteinGrams, fatsGrams, carbsGrams};
    }

    public BigDecimal calculateWeeklyRateByGoalDate(int userId, BigDecimal targetWeightKg, LocalDate targetDate) {
        BigDecimal currentWeight = weightLogDAO.getAbsoluteLatestWeight(userId);
        if (currentWeight == null) {
            throw new IllegalArgumentException("Текущий вес не найден. Запишите свой вес для расчета цели.");
        }

        LocalDate today = LocalDate.now();
        if (targetDate.isBefore(today)) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalWeightChange = targetWeightKg.subtract(currentWeight);


        long daysBetween = ChronoUnit.DAYS.between(today, targetDate);
        if (daysBetween == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal dailyRate = totalWeightChange.divide(new BigDecimal(daysBetween), 4, RoundingMode.HALF_UP);


        return dailyRate.multiply(new BigDecimal(DAYS_IN_WEEK))
                .setScale(2, RoundingMode.HALF_UP);
    }
}