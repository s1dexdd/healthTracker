package com.healthtracker.controller;

import com.healthtracker.dao.UserDAO;
import com.healthtracker.model.User;
import com.healthtracker.util.SceneLoader;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.event.ActionEvent;
import javafx.util.Callback;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;

public class LoginController {

    @FXML
    private TextField userIdField;

    @FXML
    private TextField usernameField;
    @FXML
    private TextField heightField;
    @FXML
    private TextField startWeightField;

    @FXML
    private Label messageLabel;

    private final UserDAO userDAO = new UserDAO();

    public LoginController() {
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        messageLabel.setText("");
        String userIdText = userIdField.getText();

        if (userIdText.isEmpty()) {
            messageLabel.setText("Пожалуйста, введите ваш UserID.");
            return;
        }

        try {
            int userId = Integer.parseInt(userIdText.trim());
            User user = userDAO.selectUser(userId);

            if (user != null) {
                messageLabel.setText("Вход выполнен успешно для пользователя: " + user.getName());
                loadMainView(userId, (Node) event.getSource());
            } else {
                messageLabel.setText("Пользователь с ID " + userId + " не найден. Проверьте ID или зарегистрируйтесь.");
            }
        } catch (NumberFormatException e) {
            messageLabel.setText("UserID должен быть целым числом.");
        } catch (Exception e) {
            messageLabel.setText("Ошибка входа: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        messageLabel.setText("");

        String name = usernameField.getText().trim();
        String heightText = heightField.getText().trim();
        String weightText = startWeightField.getText().trim();

        if (name.isEmpty() || heightText.isEmpty() || weightText.isEmpty()) {
            messageLabel.setText("Все поля регистрации (Имя, Рост, Вес) обязательны.");
            return;
        }

        try {
            int height = Integer.parseInt(heightText);
            weightText = weightText.replace(',', '.');
            BigDecimal startWeight = new BigDecimal(weightText);

            if (height <= 50 || height >= 300) {
                messageLabel.setText("Рост должен быть в диапазоне от 50 до 300 см.");
                return;
            }
            if (startWeight.compareTo(new BigDecimal("10.0")) < 0 || startWeight.compareTo(new BigDecimal("500.0")) > 0) {
                messageLabel.setText("Вес должен быть в разумном диапазоне (10 - 500 кг).");
                return;
            }

            User newUser = new User(
                    name,
                    height,
                    startWeight,
                    startWeight,
                    30,
                    User.Gender.MALE,
                    User.ActivityLevel.LIGHT
            );

            int newId = userDAO.insertUser(newUser);

            if (newId != -1) {
                messageLabel.setText("Регистрация успешна! Ваш ID: " + newId + ". Запомните его. Перенаправление...");
                clearRegistrationFields();
                loadMainView(newId, (Node) event.getSource());
            } else {
                messageLabel.setText("Ошибка регистрации. Попробуйте снова.");
            }
        } catch (NumberFormatException e) {
            messageLabel.setText("Рост и Вес должны быть числами.");
        } catch (Exception e) {
            messageLabel.setText("Критическая ошибка регистрации: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void clearRegistrationFields() {
        usernameField.clear();
        heightField.clear();
        startWeightField.clear();
    }

    private void loadMainView(int userId, Node sourceNode) {
        try {
            String fxmlPath = "/com/healthtracker/app/MainView.fxml";
            URL location = getClass().getResource(fxmlPath);

            if (location == null) {
                throw new IOException("FXML-ресурс не найден по пути: " + fxmlPath);
            }

            FXMLLoader loader = new FXMLLoader(location);
            loader.setControllerFactory(new Callback<Class<?>, Object>() {
                @Override
                public Object call(Class<?> type) {
                    if (type == MainController.class) {
                        return new MainController(userId);
                    }
                    try {
                        return type.getDeclaredConstructor().newInstance();
                    } catch (Exception exc) {
                        throw new RuntimeException(exc);
                    }
                }
            });

            Parent root = loader.load();
            String title = "Health Tracker - Главная панель (UserID: " + userId + ")";
            SceneLoader.switchToScene(sourceNode, root, title);

        } catch (IOException e) {
            System.err.println("Не удалось загрузить сцену MainView. Проверьте путь и файл FXML.");
            e.printStackTrace();
            messageLabel.setText("Критическая ошибка: Не удалось загрузить главное окно.");
        }
    }
}
