package com.healthtracker.controller;

import com.healthtracker.dao.WeightLogDAO;
import com.healthtracker.model.WeightLog;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;

public class LogWeightController {

    @FXML private Label dateLabel;
    @FXML private TextField weightField;

    private int activeUserId;
    private LocalDate logDate;
    private final WeightLogDAO weightLogDAO;

    public LogWeightController(WeightLogDAO weightLogDAO) {
        this.weightLogDAO = weightLogDAO;
    }

    public void initializeData(int userId, LocalDate selectedDate) {
        this.activeUserId = userId;
        this.logDate = selectedDate;
        dateLabel.setText("Дата: " + selectedDate.toString());
        weightField.requestFocus();
    }

    @FXML
    private void handleSaveWeight() {
        try {
            String weightText = weightField.getText().trim().replaceAll(",", ".");

            if (weightText.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Ошибка ввода", null, "Пожалуйста, введите ваш текущий вес.");
                return;
            }

            BigDecimal currentWeight = new BigDecimal(weightText);

            if (currentWeight.compareTo(BigDecimal.ZERO) <= 0) {
                showAlert(Alert.AlertType.WARNING, "Ошибка ввода", null, "Вес должен быть положительным числом.");
                return;
            }

            WeightLog newLog = new WeightLog(
                    activeUserId,
                    Date.valueOf(logDate),
                    currentWeight
            );

            weightLogDAO.insertWeightLog(newLog);

            showAlert(Alert.AlertType.INFORMATION, "Успех", null,
                    String.format("Вес %.1f кг успешно записан на %s.", currentWeight.floatValue(), logDate.toString()));

            handleCancel();

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Ошибка ввода", null, "Пожалуйста, введите корректное число для веса (например, 75.5).");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Ошибка сохранения", "Не удалось сохранить запись.", "Произошла ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) weightField.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
