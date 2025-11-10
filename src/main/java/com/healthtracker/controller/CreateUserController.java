package com.healthtracker.controller;

import com.healthtracker.dao.UserDAO;
import com.healthtracker.dao.WeightLogDAO;
import com.healthtracker.model.User;
import com.healthtracker.model.WeightLog;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.function.Consumer;

public class CreateUserController {

    @FXML private TextField nameField;
    @FXML private TextField heightField;
    @FXML private TextField startWeightField;
    @FXML private TextField targetWeightField;
    @FXML private TextField ageField;
    @FXML private ComboBox<String> genderComboBox;
    @FXML private ComboBox<String> activityLevelComboBox;
    @FXML private Label messageLabel;

    private final UserDAO userDAO;
    private final WeightLogDAO weightLogDAO;
    private final Consumer<Integer> registrationSuccessCallback;

    public CreateUserController(UserDAO userDAO, WeightLogDAO weightLogDAO, Consumer<Integer> registrationSuccessCallback) {
        this.userDAO = userDAO;
        this.weightLogDAO = weightLogDAO;
        this.registrationSuccessCallback = registrationSuccessCallback;
    }

    @FXML
    public void initialize() {
        genderComboBox.getItems().addAll(Arrays.stream(User.Gender.values()).map(Enum::name).toList());
        activityLevelComboBox.getItems().addAll(Arrays.stream(User.ActivityLevel.values()).map(Enum::name).toList());
        genderComboBox.getSelectionModel().select(0);
        activityLevelComboBox.getSelectionModel().select(User.ActivityLevel.MID.name());
    }

    @FXML
    private void handleSaveUser() {
        messageLabel.setText("");

        try {
            String name = nameField.getText();
            int heightCm = Integer.parseInt(heightField.getText());
            BigDecimal startWeightKg = new BigDecimal(startWeightField.getText().replaceAll(",", "."));
            BigDecimal targetWeightKg = new BigDecimal(targetWeightField.getText().replaceAll(",", "."));
            int age = Integer.parseInt(ageField.getText());
            User.Gender gender = User.Gender.valueOf(genderComboBox.getValue());
            User.ActivityLevel activityLevel = User.ActivityLevel.valueOf(activityLevelComboBox.getValue());

            if (name.trim().isEmpty() || heightCm <= 0 || startWeightKg.doubleValue() <= 0 || targetWeightKg.doubleValue() <= 0 || age <= 0) {
                messageLabel.setText("Ошибка: Все поля должны быть заполнены и содержать корректные положительные значения.");
                return;
            }

            User newUser = new User(name, heightCm, startWeightKg, targetWeightKg, age, gender, activityLevel);
            int id = userDAO.insertUser(newUser);

            if (id > 0) {
                WeightLog initialWeightLog = new WeightLog(id, Date.valueOf(LocalDate.now()), startWeightKg);
                weightLogDAO.insertWeightLog(initialWeightLog);
                messageLabel.setText("Успех! Ваш UserID: " + id + ". Запомните его.");
                registrationSuccessCallback.accept(id);
                closeWindow();
            } else {
                messageLabel.setText("Ошибка базы данных: Не удалось создать пользователя.");
            }

        } catch (NumberFormatException e) {
            messageLabel.setText("Ошибка: Рост, вес и возраст должны быть числами.");
        } catch (IllegalArgumentException e) {
            messageLabel.setText("Ошибка: Проверьте выбор пола и уровня активности.");
        } catch (Exception e) {
            messageLabel.setText("Неизвестная ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }
}
