package com.healthtracker.app;

import com.healthtracker.dao.*;
import com.healthtracker.init.DatabaseInitializer;
import com.healthtracker.model.FoodLog;
import com.healthtracker.model.User;
import com.healthtracker.model.WeightLog;
import com.healthtracker.model.WorkoutLog;
import com.healthtracker.dao.BMRDAO;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public class Main {
    public static void main(String[] args) {


        DatabaseInitializer.initialise();
        System.out.println("\n");


        UserDAO userDAO = new UserDAO();
        WeightLogDAO weightLogDAO = new WeightLogDAO();
        FoodLogDAO foodLogDAO = new FoodLogDAO();
        WorkoutLogDAO workoutLogDAO=new WorkoutLogDAO();
        BMRDAO bmrdao=new BMRDAO(userDAO,weightLogDAO);
        ReportDAO reportDAO=new ReportDAO(foodLogDAO,workoutLogDAO,bmrdao,userDAO);



        System.out.println("--- Тест 1: Пользователь и Вес ---");
        User danil = new User("Matveev Danil", 184, new BigDecimal("85.50"), new BigDecimal("70.00"),19,User.Gender.MALE,User.ActivityLevel.LIGHT);
        int userId = userDAO.insertUser(danil);
        System.out.println("Пользователь добавлен (ID: " + userId + ")");
        User retrievedUser = userDAO.selectUser(userId);
        System.out.println("Пользователь извлечен: " + retrievedUser);

        Date logDate1 = Date.valueOf(LocalDate.now().minusDays(2));
        Date logDate2 = Date.valueOf(LocalDate.now().minusDays(1));

        weightLogDAO.insertWeightLog(new WeightLog(userId, logDate1, new BigDecimal("84.0")));
        weightLogDAO.insertWeightLog(new WeightLog(userId, logDate2, new BigDecimal("83.5")));

        BigDecimal currentWeight = weightLogDAO.getLatestWeight(userId);
        System.out.println("Текущий вес: " + (currentWeight != null ? currentWeight.setScale(2) + " кг" : "нет данных"));



        System.out.println("\n--- Тест 2: Логирование питания (Автоматическое время + Тип приема пищи) ---");


        Date today = Date.valueOf(LocalDate.now());


        FoodLog meal = new FoodLog(
                userId, "Овсянка с ягодами", "Завтрак", 350,
                new BigDecimal("12.0"), new BigDecimal("7.5"), new BigDecimal("60.0"),
                80);
        foodLogDAO.insertFoodLog(meal);


        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}





        int expectedTotalFood = 280;
        int totalCaloriesToday = foodLogDAO.getDailyTotalCalories(userId, today);
        System.out.println("\nСуммарные калории за СЕГОДНЯ: " + totalCaloriesToday + " ккал");

        if (totalCaloriesToday == expectedTotalFood) {
            System.out.println(" Тест 2 УРА! Сумма калорий за день верна (" + expectedTotalFood + " ккал).");
        } else {
            System.out.println("Тест 2 ПРОВАЛ. Ожидалось " + expectedTotalFood + " ккал, получено: " + totalCaloriesToday + " ккал");
        }


        System.out.println("\n--- Тест 3: История питания за сегодня (по времени) ---");
        List<FoodLog> todayLogs = foodLogDAO.getFoodLogsByDate(userId, today);
        System.out.println("Всего записей за сегодня: " + todayLogs.size());

        if (todayLogs.size() == 1) {
            System.out.println("Тест 3 УРА! Количество записей (1) верное.");
            System.out.println("--- История:");
            for (FoodLog log : todayLogs) {

                System.out.println("   - [" + log.getLogDate().toLocalDateTime().toLocalTime() + "] "
                        + log.getMealType() + ": " + log.getDescription() +
                        " | Ккал: " + log.calculateTotalCalories());
            }
        } else {
            System.out.println("Тест 3 ПРОВАЛ. Ожидалось: 1 запись.");
        }
        System.out.println("\n--- Тест 4: Активность ---\n");

        WorkoutLog runLog=new WorkoutLog(userId,"бег",45,10);
        workoutLogDAO.insertWorkoutLog(runLog);

        try {Thread.sleep(1000);}catch (InterruptedException ignored){}

        WorkoutLog yogaLog=new WorkoutLog(userId,"йога",30,5);
        workoutLogDAO.insertWorkoutLog(yogaLog);

        int expectedTotalBurned = 450 + 150;
        int totalCaloriesBurnedToday = workoutLogDAO.getDailyTotalBurned(userId, today);
        System.out.println("\nСуммарные сожженные калории за СЕГОДНЯ: " + totalCaloriesBurnedToday + " ккал");

        if (totalCaloriesBurnedToday == expectedTotalBurned) {
            System.out.println("Тест 4 УРА! Сумма сожженных калорий за день верна (" + expectedTotalBurned + " ккал).");
        } else {
            System.out.println("Тест 4 ПРОВАЛ. Ожидалось " + expectedTotalBurned + " ккал, получено: " + totalCaloriesBurnedToday + " ккал");
        }

        System.out.println("\n--- Тест 5: История Активности ---\n");
        List<WorkoutLog> todayWorkoutLogs=workoutLogDAO.getWorkoutLogsByDate(userId,today);
        System.out.println("всего записей за сегодня: " + todayWorkoutLogs.size());

        if (todayWorkoutLogs.size()==2){
            System.out.println("Тест 5 УРА! количество записей активности верно");
            System.out.println("---История");
            for (WorkoutLog log: todayWorkoutLogs){
                System.out.println("- [" + log.getLogDate().toLocalDateTime()+"] " + log.getType() + " (" + log.getDurationMinutes() + " мин)" + "| интенсивность: " + log.getCaloriesBurnedPerMinute() + "ккал/мин" + " |"  + " сожжено: " + log.getCaloriesBurned()+ " ккал");

            }
        }else System.out.println("Тест 5 ПРОВАЛ. Ожидалось: 2 запись");
        System.out.println("\n--- Тест 6: Расчет BMR ---\n");
        int calculateBMR=bmrdao.calculateBMR(userId);
        int expectedBMR = 1982;
        System.out.println("Расчетный базовый метаболизм (BMR): " + calculateBMR + " ккал");
        if(Math.abs(calculateBMR-expectedBMR)<=1){
            System.out.println("Тест 6 УРА! BMR рассчитан корректно.");
        }else{
            System.out.println("Тест 6 ПРОВАЛ. Ожидалось около " + expectedBMR + " ккал, получено: " + calculateBMR + " ккал.");
        }
        System.out.println("\n--- Тест 7: Расчет NEAT (Фоновая активность без тренировок) ---\n");
        int totalDailyExpenditure= reportDAO.calculateDailyTotalExpenditure(userId,today);
        int expectedTotalDailyExpenditure = 3325;
        int calculateNEAT=totalDailyExpenditure-totalCaloriesBurnedToday;
        int expectedNEAT=2725;
        float coefficient=retrievedUser.getActivityLevel().getCoefficient();
        System.out.println("Уровень фоновой активности: " + retrievedUser.getActivityLevel().name() + " (Коэффициент: " + coefficient + ")");
        System.out.println("Расчетный расход (BMR + NEAT): " + calculateNEAT + " ккал");
        if (calculateNEAT==expectedNEAT){
            System.out.println("Тест 7 УРА! NEAT рассчитан корректно.");
        }else {
            System.out.println("Тест 7 ПРОВАЛ. Ожидалось: " + expectedNEAT + " ккал, получено: " + calculateNEAT + " ккал.");
        }
        System.out.println("\n--- Тест 8: Расчет Общего расхода  ---\n");
        int dailyNetCalories=reportDAO.calculateDailyNetCalories(userId,today);
        int expectedNetCalories=expectedTotalFood-expectedTotalDailyExpenditure;
        System.out.println("Общий Расход (TDEE: NEAT + Workout): " + totalDailyExpenditure + " ккал");
        System.out.println("Баланс: Потребление (" + totalCaloriesToday + ") - Расход (" + totalDailyExpenditure + ") = " + dailyNetCalories + " ккал");


        if (dailyNetCalories == expectedNetCalories) {
            String status = dailyNetCalories < 0 ? "дефицит" : "профицит";
            System.out.println("Тест 8 УРА! Энергетический баланс верный (" + status + ") равен: " + dailyNetCalories);
        } else {
            System.out.println(("Тест 8 ПРОВАЛ. Ожидалось: " + expectedNetCalories + ", получено: " + dailyNetCalories));
        }

        System.out.println("\n--- КОНЕЦ ТЕСТОВ ---");
    }
}
