package com.healthtracker.controller;

import com.healthtracker.dao.WorkoutLogDAO;
import com.healthtracker.model.WorkoutLog;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Date;

public class LogWorkoutController {

    @FXML private TextField typeField;
    @FXML private TextField durationField;
    @FXML private TextField caloriesPerMinuteField;
    @FXML private Label messageLabel;

    private final WorkoutLogDAO workoutLogDAO;
    private final int userId;
    private final Date logDate;


    public LogWorkoutController(WorkoutLogDAO workoutLogDAO, int userId, Date logDate) {
        this.workoutLogDAO = workoutLogDAO;
        this.userId = userId;
        this.logDate = logDate;
    }

    @FXML
    private void handleSaveAction() {
        try {
            String type = typeField.getText();
            int durationMinutes = Integer.parseInt(durationField.getText());
            int caloriesPerMinute = Integer.parseInt(caloriesPerMinuteField.getText());

            if (type.trim().isEmpty() || durationMinutes <= 0 || caloriesPerMinute <= 0) {
                messageLabel.setText("Пожалуйста, заполните все поля корректно.");
                return;
            }

            WorkoutLog log = new WorkoutLog(
                    userId,
                    type,
                    durationMinutes,
                    caloriesPerMinute
            );
            workoutLogDAO.insertWorkoutLog(log);

            messageLabel.setText("Тренировка успешно добавлена!");
            closeWindow();

        } catch (NumberFormatException e) {
            messageLabel.setText("Ошибка: Длительность и Калории должны быть числами.");
        } catch (Exception e) {
            messageLabel.setText("Ошибка при сохранении: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) typeField.getScene().getWindow();
        stage.close();
    }
}