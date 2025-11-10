package com.healthtracker.dao;

import com.healthtracker.model.WorkoutLog;
import com.healthtracker.util.DBConfig;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class WorkoutLogDAO {


    private static final String INSERT_LOG_SQL =
            "INSERT INTO \"WORKOUT_LOG\" (user_id, log_date, type, duration_minutes, calories_burned, calories_burned_per_minute)" +
                    "VALUES(?, ?, ?, ?, ?, ?)";


    private static final String SELECT_LOGS_BY_DATE =
            "SELECT workout_id, user_id, log_date, type, duration_minutes, calories_burned, calories_burned_per_minute FROM \"WORKOUT_LOG\" WHERE user_id = ? AND CAST(log_date AS DATE) = CAST(? AS DATE) ORDER BY log_date ASC";


    private static final String SELECT_BURNED_CALORIES_BY_DATE =
            "SELECT SUM(calories_burned) AS total_burned FROM \"WORKOUT_LOG\" WHERE user_id = ? AND CAST(log_date AS DATE) = CAST(? AS DATE)";


    public void insertWorkoutLog(WorkoutLog log){

        int totalBurned = log.calculateTotalBurned();

        try (Connection connection = DBConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_LOG_SQL)) {

            preparedStatement.setInt(1, log.getUserId());
            preparedStatement.setTimestamp(2, log.getLogDate());
            preparedStatement.setString(3, log.getType());
            preparedStatement.setInt(4, log.getDurationMinutes());
            preparedStatement.setInt(5, totalBurned);
            preparedStatement.setInt(6, log.getCaloriesBurnedPerMinute());

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.err.println("ошибка при вставке записи о тренировке: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public int getDailyTotalBurned(int userId, Date date) {
        int totalBurned = 0;
        try (Connection connection = DBConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BURNED_CALORIES_BY_DATE)) {

            preparedStatement.setInt(1, userId);
            preparedStatement.setDate(2, new java.sql.Date(date.getTime()));

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    totalBurned = rs.getInt("total_burned");
                }
            }
        } catch (SQLException e) {
            System.err.println("ошибка при получении калорий сожженных за день: " + e.getMessage());
            e.printStackTrace();
        }
        return totalBurned;
    }

    public List<WorkoutLog> getWorkoutLogsByDate(int userId, Date date){
        List<WorkoutLog> logs = new ArrayList<>();

        try(Connection connection = DBConfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_LOGS_BY_DATE)){

            preparedStatement.setInt(1, userId);
            preparedStatement.setDate(2, new java.sql.Date(date.getTime()));

            try(ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()){

                    WorkoutLog log = new WorkoutLog(
                            rs.getInt("workout_id"),
                            rs.getInt("user_id"),
                            rs.getTimestamp("log_date"),
                            rs.getString("type"),
                            rs.getInt("duration_minutes"),
                            rs.getInt("calories_burned"),
                            rs.getInt("calories_burned_per_minute")
                    );
                    logs.add(log);
                }
            }
        } catch (SQLException e){
            System.err.println("ошибка при получении истории активности: " + e.getMessage());
            e.printStackTrace();
        }
        return logs;
    }
}