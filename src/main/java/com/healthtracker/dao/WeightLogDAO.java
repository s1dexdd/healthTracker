package com.healthtracker.dao;

import com.healthtracker.model.WeightLog;
import com.healthtracker.util.DBConfig;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class WeightLogDAO {
    private static final String INSERT_LOG_SQL =
            "INSERT INTO WEIGHT_LOG ( user_id, log_date, current_weight_kg ) VALUES (?, ?, ?)";

    private static final String SELECT_LATEST_WEIGHT_SQL =
            "SELECT current_weight_kg FROM WEIGHT_LOG WHERE user_id= ? ORDER BY log_date DESC, log_id DESC LIMIT 1";

    private static final String SELECT_WEIGHT_ON_OR_BEFORE_DATE =
            "SELECT current_weight_kg FROM WEIGHT_LOG WHERE user_id = ? AND log_date <= ? ORDER BY log_date DESC, log_id DESC LIMIT 1";


    private static final String SELECT_ALL_LOGS_SQL =
            "SELECT log_id, user_id, log_date, current_weight_kg FROM WEIGHT_LOG WHERE user_id= ? ORDER BY log_date DESC, log_id DESC";


    private static final String DELETE_LOG_SQL =
            "DELETE FROM WEIGHT_LOG WHERE log_id = ?";


    public void insertWeightLog(WeightLog log) {
        try (Connection connection = DBConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_LOG_SQL)) {
            preparedStatement.setInt(1, log.getUserId());
            preparedStatement.setDate(2, log.getLogDate());
            preparedStatement.setBigDecimal(3, log.getCurrentWeightKg());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Ошибка при вставке записи веса: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public List<WeightLog> getWeightLogsByUserId(int userId) {
        List<WeightLog> logs = new ArrayList<>();
        try (Connection connection = DBConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_LOGS_SQL)) {
            preparedStatement.setInt(1, userId);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {

                    WeightLog log = new WeightLog(
                            rs.getInt("log_id"),
                            rs.getInt("user_id"),
                            rs.getDate("log_date"),
                            rs.getBigDecimal("current_weight_kg")
                    );
                    logs.add(log);
                }
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении истории веса: " + e.getMessage());
            e.printStackTrace();
        }
        return logs;
    }

    public void deleteWeightLog(int logId) throws SQLException {
        try (Connection connection = DBConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_LOG_SQL)) {
            preparedStatement.setInt(1, logId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Ошибка при удалении записи веса с ID " + logId + ": " + e.getMessage());

            throw e;
        }
    }

    public BigDecimal getAbsoluteLatestWeight(int userId) {
        return getWeightFromQuery(SELECT_LATEST_WEIGHT_SQL, userId, null);
    }

    public BigDecimal getLatestWeightOnOrBeforeDate(int userId, Date date) {
        return getWeightFromQuery(SELECT_WEIGHT_ON_OR_BEFORE_DATE, userId, date);
    }

    private BigDecimal getWeightFromQuery(String sql, int userId, Date date) {
        BigDecimal latestWeight = null;
        try (Connection connection = DBConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, userId);

            if (sql.equals(SELECT_WEIGHT_ON_OR_BEFORE_DATE) && date != null) {
                preparedStatement.setDate(2, date);
            }

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    latestWeight = rs.getBigDecimal("current_weight_kg");
                }
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении веса из запроса: " + e.getMessage());
            e.printStackTrace();
        }
        return latestWeight;
    }
}