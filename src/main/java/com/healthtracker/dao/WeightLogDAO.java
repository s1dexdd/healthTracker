package com.healthtracker.dao;

import com.healthtracker.model.WeightLog;
import com.healthtracker.util.DBConfig;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;

public class WeightLogDAO {
    private static final String INSERT_LOG_SQL =
            // ✅ ИСПРАВЛЕНО: Таблица теперь WEIGHT_LOG
            "INSERT INTO WEIGHT_LOG ( user_id, log_date, current_weight_kg ) VALUES (?, ?, ?)";
    private static final String SELECT_LATEST_WEIGHT_SQL =
            // ✅ ИСПРАВЛЕНО: Таблица теперь WEIGHT_LOG
            "SELECT current_weight_kg FROM WEIGHT_LOG WHERE user_id= ? ORDER BY log_date DESC, log_id DESC LIMIT 1";

    public void insertWeightLog(WeightLog log) {
        try (Connection connection = DBConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_LOG_SQL)) {
            preparedStatement.setInt(1, log.getUserId());
            preparedStatement.setDate(2, log.getLogDate());
            preparedStatement.setBigDecimal(3, log.getCurrentWeightKg());
            preparedStatement.executeUpdate();
            System.out.println("запись веса для UserID " + log.getUserId() + " успешно добавлено: " + log.getCurrentWeightKg());
        } catch (SQLException e) {
            System.err.println("ошибка при добавлении записи веса");
            e.printStackTrace();
        }
    }

    // Входящий параметр исправлен на userId (маленькая буква)
    public BigDecimal getLatestWeight(int userId) {
        BigDecimal latestWeight = null;
        try (Connection connection = DBConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_LATEST_WEIGHT_SQL)) {
            preparedStatement.setInt(1, userId);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    latestWeight = rs.getBigDecimal("current_weight_kg");
                }
            }
        } catch (SQLException e) {
            System.err.println("ошибка при получении последнего веса: " + e.getMessage());
            e.printStackTrace();
        }
        return latestWeight;
    }
}
