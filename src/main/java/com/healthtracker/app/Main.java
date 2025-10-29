package com.healthtracker.app;

import com.healthtracker.init.DatabaseInitializer;
import com.healthtracker.dao.UserDAO;
import com.healthtracker.model.User;
import com.healthtracker.model.WeightLog;
import com.healthtracker.dao.WeightLogDAO;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;

public class Main {
    public static void main(String[] args){

        // ИНИЦИАЛИЗАЦИЯ БАЗЫ ДАННЫХ
        DatabaseInitializer.initialise();

        // ------------------------------------------
        //          ТЕСТ 1 & 2: UserDAO
        // ------------------------------------------
        System.out.println("\n тест добавления пользователя \n");

        BigDecimal startWeight = new BigDecimal("85.5");
        BigDecimal targetWeight = new BigDecimal("70.0");
        UserDAO userDAO = new UserDAO();
        User newUser = new User(
                "Matveev Danil",
                184,
                startWeight,
                targetWeight
        );
        int generatedUserId = userDAO.insertUser(newUser);

        System.out.println("\n тест 1 УРА \n");

        System.out.println("\n тест извлечения пользователя \n");
        User fetchedUser = userDAO.selectUser(generatedUserId);

        int testingUserId = -1;

        if (fetchedUser != null){
            System.out.println("тест 2 УРА");
            System.out.println(fetchedUser);
            testingUserId = fetchedUser.getUserId();

            // ------------------------------------------
            //          ТЕСТ 3 & 4: WeightLogDAO
            // ------------------------------------------
            System.out.println("\n--- Тест 3: Логирование веса ---");
            WeightLogDAO weightLogDAO = new WeightLogDAO();

            // 1. Запись старого веса
            LocalDate twoWeeksAgo = LocalDate.now().minusDays(14);
            WeightLog logOld = new WeightLog(
                    testingUserId,
                    Date.valueOf(twoWeeksAgo),
                    new BigDecimal("84.0")
            );
            weightLogDAO.insertWeightLog(logOld);

            // 2. Запись текущего веса (сегодня)
            LocalDate today = LocalDate.now();
            WeightLog logToday = new WeightLog(
                    testingUserId,
                    Date.valueOf(today),
                    new BigDecimal("83.5")
            );
            weightLogDAO.insertWeightLog(logToday);

            // --- Тест 4: Получение текущего веса ---
            System.out.println("\n--- Тест 4: Получение текущего веса ---");


            BigDecimal currentWeight = weightLogDAO.getLatestWeight(testingUserId);

            if(currentWeight != null) {
                System.out.println("Текущий вес: " + currentWeight + " кг");
            } else {
                System.out.println("Не удалось получить текущий вес.");
            }

        } else {
            System.out.println("эх id не найден");
        }
        System.out.println("\n конец \n");
    }
}