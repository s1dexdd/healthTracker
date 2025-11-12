package com.healthtracker.controller;

import com.healthtracker.dao.UserDAO;
import com.healthtracker.dao.WeightLogDAO;
import com.healthtracker.model.User;
import com.healthtracker.model.WeightLog;
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



public class MainController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private TabPane mainTabPane;

    @FXML private DatePicker logDatePicker; // Соответствует fx:id="logDatePicker"
    @FXML private TextField weightInput;
    @FXML private Label weightStatusLabel; // Соответствует fx:id="weightStatusLabel"
    @FXML private Label latestWeightLabel;
    @FXML private Label startWeightLabel;
    @FXML private Label targetWeightLabel;
    @FXML private Label bmiLabel;
    @FXML private TableView<WeightLog> weightLogTable;
    @FXML private TableColumn<WeightLog, Date> colLogDate; // Соответствует fx:id="colLogDate"
    @FXML private TableColumn<WeightLog, BigDecimal> colCurrentWeight; // Соответствует fx:id="colCurrentWeight"
    @FXML private TableColumn<WeightLog, Integer> colLogId; // colLogId не используется в таблице, но объявлен.
    @FXML private Button deleteLogButton;


    private final int currentUserId;
    private User currentUser;
    private final UserDAO userDAO = new UserDAO();
    private final WeightLogDAO weightLogDAO = new WeightLogDAO();
    private final ObservableList<WeightLog> weightLogData = FXCollections.observableArrayList();

    public MainController(int userId) {
        this.currentUserId = userId;
    }


    @FXML
    public void initialize() {

        this.currentUser = userDAO.selectUser(currentUserId);


        if (currentUser != null) {
            welcomeLabel.setText("Добро пожаловать, " + currentUser.getName() + " (ID: " + currentUserId + ")");
            // !!! ДОБАВЛЕН ВЫЗОВ ДЛЯ ИНИЦИАЛИЗАЦИИ ВКЛАДКИ ВЕСА
            initializeWeightTab();

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

        // colLogId.setCellValueFactory(new PropertyValueFactory<>("logId")); // Эта колонка скрыта в FXML
        colLogDate.setCellValueFactory(new PropertyValueFactory<>("logDate"));
        colCurrentWeight.setCellValueFactory(new PropertyValueFactory<>("currentWeightKg"));


        weightLogTable.setItems(weightLogData);


        // Добавляем слушатель для активации/деактивации кнопки удаления
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


        BigDecimal bmi = calculateBMI(latestWeight, currentUser.getHeightCm());
        bmiLabel.setText(bmi.toString());


        String bmiCategory = getBMICategory(bmi);
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




    private BigDecimal calculateBMI(BigDecimal weightKg, int heightCm) {
        if (heightCm == 0) return BigDecimal.ZERO;

        BigDecimal heightM = new BigDecimal(heightCm).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        BigDecimal heightSquared = heightM.multiply(heightM);

        if (heightSquared.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;

        return weightKg.divide(heightSquared, 2, RoundingMode.HALF_UP);
    }


    private String getBMICategory(BigDecimal bmi) {
        if (bmi.compareTo(new BigDecimal("18.5")) < 0) return "Недостаточный вес";
        if (bmi.compareTo(new BigDecimal("25.0")) < 0) return "Нормальный вес";
        if (bmi.compareTo(new BigDecimal("30.0")) < 0) return "Избыточный вес (Предожирение)";
        if (bmi.compareTo(new BigDecimal("35.0")) < 0) return "Ожирение I степени";
        if (bmi.compareTo(new BigDecimal("40.0")) < 0) return "Ожирение II степени";
        return "Ожирение III степени";
    }


}