package com.healthtracker.controller;

import com.healthtracker.dao.UserDAO;
import com.healthtracker.model.GoalResult;
import com.healthtracker.model.User;
import com.healthtracker.service.GoalCalculationService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.math.BigDecimal;

public class GoalController {

    @FXML private TextField weeklyRateInput;
    @FXML private TextField targetWeightInput;
    @FXML private ComboBox<User.ActivityLevel> activityLevelComboBox;
    @FXML private Button calculateButton;
    @FXML private Button saveButton;
    @FXML private TextArea resultArea;
    @FXML private Label currentGoalLabel;
    @FXML private Label messageLabel;

    private final GoalCalculationService goalCalculationService;
    private final UserDAO userDAO;
    private int currentUserId = -1;
    private GoalResult lastCalculatedResult;

    public GoalController(GoalCalculationService goalCalculationService, UserDAO userDAO) {
        this.goalCalculationService = goalCalculationService;
        this.userDAO = userDAO;
    }

    @FXML
    public void initialize() {
        activityLevelComboBox.setItems(FXCollections.observableArrayList(User.ActivityLevel.values()));
        if (currentUserId != -1) {
            loadCurrentGoal();
        }
        saveButton.setDisable(true);
    }

    private void loadCurrentGoal() {
        if (currentUserId == -1) {
            currentGoalLabel.setText("Ошибка: ID пользователя не установлен.");
            return;
        }

        User user = userDAO.selectUser(currentUserId);
        if (user != null) {
            currentGoalLabel.setText(String.format(
                    "Текущий Целевой Вес: %.2f кг, Уровень Активности: %s",
                    user.getTargetWeightKg().floatValue(),
                    user.getActivityLevel().name()
            ));
            targetWeightInput.setText(user.getTargetWeightKg().toPlainString());
            activityLevelComboBox.setValue(user.getActivityLevel());
        } else {
            currentGoalLabel.setText("Пользователь с ID " + currentUserId + " не найден.");
            targetWeightInput.setText("0.0");
        }
    }

    @FXML
    private void handleCalculateGoal() {
        if (currentUserId == -1) {
            messageLabel.setText("Ошибка: ID пользователя не установлен. Невозможно выполнить расчет.");
            return;
        }

        messageLabel.setText("");
        resultArea.setText("");
        saveButton.setDisable(true);

        try {
            BigDecimal weeklyRate = new BigDecimal(weeklyRateInput.getText().replaceAll(",", "."));
            lastCalculatedResult = goalCalculationService.calculateTargetIntakeByWeeklyRate(currentUserId, weeklyRate);
            resultArea.setText(lastCalculatedResult.toString());
            messageLabel.setText("Расчет успешно выполнен. Нажмите 'Сохранить', чтобы применить цель.");
            saveButton.setDisable(false);
        } catch (NumberFormatException e) {
            messageLabel.setText("Ошибка: Неверный формат числа для недельной нормы.");
            resultArea.setText("");
            lastCalculatedResult = null;
        } catch (IllegalArgumentException e) {
            messageLabel.setText("Ошибка расчета: " + e.getMessage());
            resultArea.setText("");
            lastCalculatedResult = null;
        } catch (Exception e) {
            messageLabel.setText("Произошла непредвиденная ошибка: " + e.getMessage());
            e.printStackTrace();
            lastCalculatedResult = null;
        }
    }

    @FXML
    private void handleSaveGoal() {
        if (currentUserId == -1) {
            messageLabel.setText("Ошибка: ID пользователя не установлен. Невозможно сохранить цель.");
            return;
        }

        if (lastCalculatedResult == null) {
            messageLabel.setText("Сначала необходимо рассчитать цель.");
            return;
        }

        try {
            BigDecimal newTargetWeight = new BigDecimal(targetWeightInput.getText().replaceAll(",", "."));
            User.ActivityLevel newActivityLevel = activityLevelComboBox.getValue();

            if (newActivityLevel == null) {
                messageLabel.setText("Пожалуйста, выберите уровень активности.");
                return;
            }

            if (newTargetWeight.doubleValue() <= 0) {
                messageLabel.setText("Целевой вес должен быть положительным числом.");
                return;
            }

            boolean success = userDAO.updateUserGoal(currentUserId, newTargetWeight, newActivityLevel);

            if (success) {
                messageLabel.setText("Цель успешно сохранена! Рекомендованное потребление: " + lastCalculatedResult.getTargetIntakeKcal() + " ккал.");
                saveButton.setDisable(true);
                loadCurrentGoal();
            } else {
                messageLabel.setText("Ошибка при сохранении цели в базу данных.");
            }
        } catch (NumberFormatException e) {
            messageLabel.setText("Ошибка: Неверный формат числа для целевого веса.");
        } catch (Exception e) {
            messageLabel.setText("Произошла ошибка при сохранении: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void setCurrentUserId(int userId) {
        this.currentUserId = userId;
        if (currentGoalLabel != null) {
            loadCurrentGoal();
        }
    }
}
