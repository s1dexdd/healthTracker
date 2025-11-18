package com.healthtracker.controller;

import com.healthtracker.dao.UserDAO;
import com.healthtracker.dao.WeightLogDAO;
import com.healthtracker.model.User;
import com.healthtracker.model.WeightLog;
import com.healthtracker.dao.FoodLogDAO;
import com.healthtracker.dao.WorkoutLogDAO;
import com.healthtracker.dao.BMRDAO;
import com.healthtracker.dao.ReportDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.event.ActionEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.sql.SQLException;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.VBox;



public class MainController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private TabPane mainTabPane;

    @FXML private DatePicker logDatePicker;
    @FXML private TextField weightInput;
    @FXML private Label weightStatusLabel;
    @FXML private Label latestWeightLabel;
    @FXML private Label startWeightLabel;
    @FXML private Label targetWeightLabel;
    @FXML private Label bmiLabel;
    @FXML private TableView<WeightLog> weightLogTable;
    @FXML private TableColumn<WeightLog, Date> colLogDate;
    @FXML private TableColumn<WeightLog, BigDecimal> colCurrentWeight;
    @FXML private TableColumn<WeightLog, Integer> colLogId;
    @FXML private Button deleteLogButton;
    @FXML private TextField settingsNameInput;
    @FXML private TextField settingsHeightInput;
    @FXML private TextField settingsStartWeightInput;
    @FXML private TextField settingsTargetWeightInput;
    @FXML private ChoiceBox<User.ActivityLevel> settingsActivityChoice;
    @FXML private Label settingsStatusLabel;
    @FXML
    private VBox chartsContainer;


    private final int currentUserId;
    private User currentUser;
    private final UserDAO userDAO = new UserDAO();
    private final WeightLogDAO weightLogDAO = new WeightLogDAO();
    private final ObservableList<WeightLog> weightLogData = FXCollections.observableArrayList();
    private final FoodLogDAO foodLogDAO = new FoodLogDAO();
    private final WorkoutLogDAO workoutLogDAO = new WorkoutLogDAO();
    private final BMRDAO bmrDAO = new BMRDAO(userDAO, weightLogDAO);
    private final ReportDAO reportDAO = new ReportDAO(foodLogDAO, workoutLogDAO, bmrDAO, userDAO, weightLogDAO);

    public MainController(int userId) {
        this.currentUserId = userId;
        this.currentUser = userDAO.selectUser(userId);
    }


    @FXML
    public void initialize() {

        this.currentUser = userDAO.selectUser(currentUserId);


        if (currentUser != null) {
            welcomeLabel.setText("Добро пожаловать, " + currentUser.getName() + " (ID: " + currentUserId + ")");

            initializeWeightTab();
            initializeSettingsTab();

        } else {

            welcomeLabel.setText("Ошибка: Пользователь не найден.");
        }
    }
    private void initializeWeightTab() {

        logDatePicker.setValue(LocalDate.now());


        startWeightLabel.setText(currentUser.getStartWeightKg().toString() + " кг");
        targetWeightLabel.setText(currentUser.getTargetWeightKg().toString() + " кг");


        setupWeightLogTable();


        loadWeightLogData();
    }
    private void setupWeightLogTable() {


        colLogDate.setCellValueFactory(new PropertyValueFactory<>("logDate"));
        colCurrentWeight.setCellValueFactory(new PropertyValueFactory<>("currentWeightKg"));


        weightLogTable.setItems(weightLogData);



        weightLogTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    deleteLogButton.setDisable(newValue == null);
                });
    }
    private void loadWeightLogData() {
        try {
            List<WeightLog> logs = weightLogDAO.getWeightLogsByUserId(currentUserId);
            weightLogData.setAll(logs);


            updateLatestWeightAndBMI();
        } catch (Exception e) {
            System.err.println("Ошибка при загрузке истории веса: " + e.getMessage());
            weightStatusLabel.setText("Ошибка: Не удалось загрузить историю веса.");
        }
    }
    @FXML
    private void handleAddWeight(ActionEvent event) {
        weightStatusLabel.setText("");


        LocalDate localDate = logDatePicker.getValue();
        String weightText = weightInput.getText().trim();

        if (localDate == null || weightText.isEmpty()) {
            weightStatusLabel.setText("Пожалуйста, введите дату и вес.");
            return;
        }

        try {

            weightText = weightText.replace(',', '.');
            BigDecimal currentWeight = new BigDecimal(weightText);


            if (currentWeight.compareTo(new BigDecimal("10.0")) < 0 || currentWeight.compareTo(new BigDecimal("500.0")) > 0) {
                weightStatusLabel.setText("Вес должен быть в разумном диапазоне (10 - 500 кг).");
                return;
            }


            Date logDate = Date.valueOf(localDate);
            WeightLog log = new WeightLog(currentUserId, logDate, currentWeight);
            weightLogDAO.insertWeightLog(log);


            weightStatusLabel.setText("Вес " + currentWeight + " кг успешно записан на " + localDate + ".");
            weightInput.clear();
            loadWeightLogData();

        } catch (NumberFormatException e) {
            weightStatusLabel.setText("Неверный формат веса. Используйте числа.");
        } catch (Exception e) {
            weightStatusLabel.setText("Ошибка при сохранении записи: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateLatestWeightAndBMI() {

        BigDecimal latestWeight = weightLogDAO.getAbsoluteLatestWeight(currentUserId);

        if (latestWeight == null) {
            latestWeight = currentUser.getStartWeightKg();
        }

        latestWeightLabel.setText(latestWeight.toString() + " кг");


        BigDecimal bmi = reportDAO.calculateBMI(currentUserId);
        bmiLabel.setText(bmi.toString());

        String bmiCategory = reportDAO.getBMICategory(bmi);
        bmiLabel.setTooltip(new javafx.scene.control.Tooltip(bmiCategory));
    }


    @FXML
    private void handleDeleteWeightLog(ActionEvent event) {
        WeightLog selectedLog = weightLogTable.getSelectionModel().getSelectedItem();

        if (selectedLog == null) {
            weightStatusLabel.setText("Пожалуйста, выберите запись для удаления.");
            return;
        }


        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Подтверждение удаления");
        alert.setHeaderText("Удалить запись о весе?");
        alert.setContentText("Вы действительно хотите удалить запись веса от " + selectedLog.getLogDate() + " (" + selectedLog.getCurrentWeightKg() + " кг)?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    weightLogDAO.deleteWeightLog(selectedLog.getLogId());
                    weightStatusLabel.setText("Запись успешно удалена.");
                    loadWeightLogData();
                } catch (SQLException e) {
                    weightStatusLabel.setText("Ошибка базы данных при удалении: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }
    private void initializeSettingsTab() {

        settingsActivityChoice.setItems(FXCollections.observableArrayList(User.ActivityLevel.values()));


        settingsNameInput.setText(currentUser.getName());
        settingsHeightInput.setText(String.valueOf(currentUser.getHeightCm()));
        settingsStartWeightInput.setText(currentUser.getStartWeightKg().toString());
        settingsTargetWeightInput.setText(currentUser.getTargetWeightKg().toString());
        settingsActivityChoice.setValue(currentUser.getActivityLevel());
    }
    @FXML
    private void handleSaveSettings(ActionEvent event) {
        settingsStatusLabel.setText("");

        try {

            String newName = settingsNameInput.getText().trim();
            int newHeight = Integer.parseInt(settingsHeightInput.getText().trim());
            BigDecimal newStartWeight = new BigDecimal(settingsStartWeightInput.getText().trim().replace(',', '.'));
            BigDecimal newTargetWeight = new BigDecimal(settingsTargetWeightInput.getText().trim().replace(',', '.'));
            User.ActivityLevel newActivityLevel = settingsActivityChoice.getValue();

            if (newName.isEmpty() || newHeight <= 0 || newStartWeight.compareTo(BigDecimal.ZERO) <= 0 || newTargetWeight.compareTo(BigDecimal.ZERO) <= 0 || newActivityLevel == null) {
                settingsStatusLabel.setText("Пожалуйста, заполните все поля корректными данными.");
                return;
            }


            boolean success = userDAO.updateUserAllSettings(
                    currentUserId,
                    newName,
                    newHeight,
                    newStartWeight,
                    newTargetWeight,
                    newActivityLevel
            );

            if (success) {

                currentUser = userDAO.selectUser(currentUserId);
                initializeSettingsTab();
                initializeWeightTab();
                welcomeLabel.setText("Добро пожаловать, " + currentUser.getName() + " (ID: " + currentUserId + ")"); // Обновляем приветствие

                settingsStatusLabel.setText("Настройки успешно сохранены!");
                settingsStatusLabel.setStyle("-fx-text-fill: #4CAF50;");


            } else {
                settingsStatusLabel.setText("Ошибка при сохранении настроек в базу данных.");
                settingsStatusLabel.setStyle("-fx-text-fill: #D32F2F;");
            }

        } catch (NumberFormatException e) {
            settingsStatusLabel.setText("Неверный формат числа для роста или веса.");
            settingsStatusLabel.setStyle("-fx-text-fill: #D32F2F;");
        } catch (Exception e) {
            settingsStatusLabel.setText("Произошла неизвестная ошибка: " + e.getMessage());
            settingsStatusLabel.setStyle("-fx-text-fill: #D32F2F;");
            e.printStackTrace();
        }

    }










}