package com.healthtracker.dao;
import com.healthtracker.dao.*;
import com.healthtracker.model.User;
import java.sql.Date;

public class ReportDAO {
    private final FoodLogDAO foodLogDAO;
    private final WorkoutLogDAO workoutLogDAO;
    private final BMRDAO bmrdao;
    private final UserDAO userDAO;

    public ReportDAO(FoodLogDAO foodLogDAO, WorkoutLogDAO workoutLogDAO, BMRDAO bmrdao, UserDAO userDAO) {
        this.foodLogDAO = foodLogDAO;
        this.workoutLogDAO = workoutLogDAO;
        this.bmrdao = bmrdao;
        this.userDAO = userDAO;
    }
    public int calculateDailyTotalExpenditure(int userId,Date date) {
        int bmr = bmrdao.calculateBMR(userId);
        User user = userDAO.selectUser(userId);
        if (user == null) {
            System.err.println("Ошибка: Пользователь с ID " + userId + " не найден.");
            return 0;
        }
        float coefficient;
        try {
            coefficient=user.getActivityLevel().getCoefficient();
        }catch (Exception e){
            System.err.println("Предупреждение: Неизвестный или устаревший уровень активности. Используется коэффициент MID (1.55).");
            coefficient = 1.55f;
        }
        int neatExpenditure=(int) Math.round(bmr * coefficient);
        int workoutExpenditure = workoutLogDAO.getDailyTotalBurned(userId, date);
        return  neatExpenditure+workoutExpenditure;
    }
    public int calculateDailyNetCalories(int userId, Date date){
        int totalFoodCalories= foodLogDAO.getDailyTotalCalories(userId,date);
        int totalBurnedCalories= calculateDailyTotalExpenditure(userId,date);
        return totalFoodCalories-totalBurnedCalories;
    }
}
