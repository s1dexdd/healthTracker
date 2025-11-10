package com.healthtracker.dao;

import com.healthtracker.model.FoodLog;
import com.healthtracker.util.DBConfig;
import java.sql.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class FoodLogDAO {

    private static final String INSERT_LOG_SQL =
            "INSERT INTO \"FOOD_LOG\" (user_id, log_date, description,meal_type, calories_per_100g, protein_per_100g, fats_per_100g, carbs_per_100g, portion_size_grams, calories, protein_g, fats_g, carbs_g) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";


    private static final String SELECT_DAILY_CALORIES_SQL =
            "SELECT SUM(calories) AS total_calories FROM \"FOOD_LOG\" WHERE user_id = ? AND CAST(log_date AS DATE) = CAST(? AS DATE)";


    private static final String SELECT_LOGS_BY_DATE =
            "SELECT food_id, user_id, log_date, description, meal_type, calories_per_100g, protein_per_100g, fats_per_100g, carbs_per_100g, portion_size_grams, calories, protein_g, fats_g, carbs_g FROM \"FOOD_LOG\" WHERE user_id = ? AND CAST(log_date AS DATE) = CAST(? AS DATE) ORDER BY log_date ASC";


    public void insertFoodLog(FoodLog log) {

        int totalCalories = log.calculateTotalCalories();
        BigDecimal totalProtein = log.calculateTotalProtein();
        BigDecimal totalFats = log.calculateTotalFats();
        BigDecimal totalCarbs = log.calculateTotalCarbs();

        try (Connection connection = DBConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_LOG_SQL)) {
            preparedStatement.setInt(1, log.getUserId());
            preparedStatement.setTimestamp(2, log.getLogDate());
            preparedStatement.setString(3, log.getDescription());
            preparedStatement.setString(4, log.getMealType());
            preparedStatement.setInt(5, log.getCaloriesPer100g());
            preparedStatement.setBigDecimal(6, log.getProteinPer100g());
            preparedStatement.setBigDecimal(7, log.getFatsPer100g());
            preparedStatement.setBigDecimal(8, log.getCarbsPer100g());
            preparedStatement.setInt(9, log.getPortionSizeGrams());
            // Новые поля для рассчитанных значений
            preparedStatement.setInt(10, totalCalories);
            preparedStatement.setBigDecimal(11, totalProtein);
            preparedStatement.setBigDecimal(12, totalFats);
            preparedStatement.setBigDecimal(13, totalCarbs);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Ошибка при вставке записи о еде: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public int getDailyTotalCalories(int userId, Date date) {
        int totalCalories = 0;
        try (Connection connection = DBConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_DAILY_CALORIES_SQL)) {

            preparedStatement.setInt(1, userId);
            preparedStatement.setDate(2, date);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    totalCalories = rs.getInt("total_calories");
                }
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении ежедневного количества калорий: " + e.getMessage());
        }
        return totalCalories;
    }


    public List<FoodLog> getFoodLogsByDate(int userId, Date date) {
        List<FoodLog> logs = new ArrayList<>();

        try (Connection connection = DBConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_LOGS_BY_DATE)) {

            preparedStatement.setInt(1, userId);
            preparedStatement.setDate(2, date);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {


                    FoodLog log = new FoodLog(
                            rs.getInt("food_id"),
                            rs.getInt("user_id"),
                            rs.getTimestamp("log_date"),
                            rs.getString("description"),
                            rs.getString("meal_type"),
                            rs.getInt("calories_per_100g"),
                            rs.getBigDecimal("protein_per_100g"),
                            rs.getBigDecimal("fats_per_100g"),
                            rs.getBigDecimal("carbs_per_100g"),
                            rs.getInt("portion_size_grams"),
                            rs.getInt("calories"),
                            rs.getBigDecimal("protein_g"),
                            rs.getBigDecimal("fats_g"),
                            rs.getBigDecimal("carbs_g")
                    );
                    logs.add(log);
                }
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении истории питания: " + e.getMessage());
            e.printStackTrace();
        }
        return logs;
    }
}