package com.healthtracker.app;

import com.healthtracker.dao.FoodLogDAO;
import com.healthtracker.dao.UserDAO;
import com.healthtracker.dao.WeightLogDAO;
import com.healthtracker.init.DatabaseInitializer;
import com.healthtracker.model.FoodLog;
import com.healthtracker.model.User;
import com.healthtracker.model.WeightLog;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class Main {
    public static void main(String[] args) {


        DatabaseInitializer.initialise();
        System.out.println("\n");


        UserDAO userDAO = new UserDAO();
        WeightLogDAO weightLogDAO = new WeightLogDAO();
        FoodLogDAO foodLogDAO = new FoodLogDAO();


        System.out.println("--- Тест 1, 2, 3, 4: Пользователь и Вес ---");
        User danil = new User("Matveev Danil", 184, new BigDecimal("85.50"), new BigDecimal("70.00"));
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



        System.out.println("\n--- Тест 5, 6, 7: Логирование питания (Автоматическое время + Тип приема пищи) ---");


        Date today = Date.valueOf(LocalDate.now());


        FoodLog breakfast = new FoodLog(
                userId, "Овсянка с ягодами", "Завтрак", 350,
                new BigDecimal("12.0"), new BigDecimal("7.5"), new BigDecimal("60.0"),
                80);
        foodLogDAO.insertFoodLog(breakfast);


        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}


        FoodLog lunch = new FoodLog(
                userId, "Курица и рис с овощами", "Обед", 110,
                new BigDecimal("25.0"), new BigDecimal("3.0"), new BigDecimal("35.0"),
                300);
        foodLogDAO.insertFoodLog(lunch);



        Timestamp tsDinnerYesterday = Timestamp.valueOf(LocalDate.now().minusDays(1).atStartOfDay().withHour(20));
        FoodLog dinnerYesterday = new FoodLog(
                -1, userId, tsDinnerYesterday, "Салат с тунцом", "Ужин", 150,
                new BigDecimal("30.0"), new BigDecimal("20.0"), new BigDecimal("15.0"),
                250);
        foodLogDAO.insertFoodLog(dinnerYesterday);


        int expectedTotal = 280 + 330; // 610 ккал
        int totalCaloriesToday = foodLogDAO.getDailyTotalCalories(userId, today);
        System.out.println("\nСуммарные калории за СЕГОДНЯ: " + totalCaloriesToday + " ккал");

        if (totalCaloriesToday == expectedTotal) {
            System.out.println(" Тест 6 УРА! Сумма калорий за день верна (" + expectedTotal + " ккал).");
        } else {
            System.out.println("Тест 6 ПРОВАЛ. Ожидалось " + expectedTotal + " ккал, получено: " + totalCaloriesToday + " ккал");
        }


        System.out.println("\n--- Тест 7: История питания за сегодня (по времени) ---");
        List<FoodLog> todayLogs = foodLogDAO.getFoodLogsByDate(userId, today);
        System.out.println("Всего записей за сегодня: " + todayLogs.size());

        if (todayLogs.size() == 2) {
            System.out.println("Тест 7 УРА! Количество записей (2) верное.");
            System.out.println("--- История:");
            for (FoodLog log : todayLogs) {

                System.out.println("   - [" + log.getLogDate().toLocalDateTime().toLocalTime() + "] "
                        + log.getMealType() + ": " + log.getDescription() +
                        " | Ккал: " + log.calculateTotalCalories());
            }
        } else {
            System.out.println("Тест 7 ПРОВАЛ. Ожидалось 2 записи.");
        }


        System.out.println("\n--- КОНЕЦ ТЕСТОВ ---");
    }
}
