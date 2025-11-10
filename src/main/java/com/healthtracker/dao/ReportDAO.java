package com.healthtracker.dao;

import com.healthtracker.model.User;
import java.sql.Date;
import java.util.Objects;

public class ReportDAO {
    private final FoodLogDAO foodLogDAO;
    private final WorkoutLogDAO workoutLogDAO;
    private final BMRDAO bmrdao;
    private final UserDAO userDAO;

    public ReportDAO(FoodLogDAO foodLogDAO, WorkoutLogDAO workoutLogDAO, BMRDAO bmrdao, UserDAO userDAO) {
        this.foodLogDAO = Objects.requireNonNull(foodLogDAO, "FoodLogDAO не должен быть null");
        this.workoutLogDAO = Objects.requireNonNull(workoutLogDAO, "WorkoutLogDAO не должен быть null");
        this.bmrdao = Objects.requireNonNull(bmrdao, "BMRDAO не должен быть null");
        this.userDAO = Objects.requireNonNull(userDAO, "UserDAO не должен быть null");
    }

    public int calculateDailyNeatExpenditure(int userId) {

        int bmr = bmrdao.calculateBMR(userId);
        User user = userDAO.selectUser(userId);
        if (user == null) {
            System.err.println("Ошибка: Пользователь с ID " + userId + " не найден.");
            return 0;
        }


        float coefficient;
        try {
            coefficient = user.getActivityLevel().getCoefficient();
        } catch (Exception e) {

            coefficient = 1.20f;
            System.err.println("Ошибка при получении коэффициента активности, установлен по умолчанию: " + coefficient);
        }


        return (int) Math.round(bmr * coefficient);
    }

    public int calculateDailyTotalExpenditure(int userId, Date date) {

        int neatExpenditure = calculateDailyNeatExpenditure(userId);


        int workoutExpenditure = workoutLogDAO.getDailyTotalBurned(userId, date);

        return neatExpenditure + workoutExpenditure;
    }

    public int calculateDailyNetCalories(int userId, Date date){

        int consumedCalories = foodLogDAO.getDailyTotalCalories(userId, date);


        int totalExpenditure = calculateDailyTotalExpenditure(userId, date);


        return consumedCalories - totalExpenditure;
    }
}