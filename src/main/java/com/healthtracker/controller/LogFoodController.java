package com.healthtracker.controller;

import com.healthtracker.dao.FoodLogDAO;
import com.healthtracker.model.FoodLog;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;

public class LogFoodController {

    @FXML private TextField descriptionField;
    @FXML private ComboBox<String> mealTypeComboBox;
    @FXML private TextField caloriesPer100gField;
    @FXML private TextField proteinPer100gField;
    @FXML private TextField fatsPer100gField;
    @FXML private TextField carbsPer100gField;
    @FXML private TextField portionSizeField;
    @FXML private DatePicker logDatePicker;
    @FXML private TextField logTimeField;
    @FXML private Label totalCaloriesLabel;

    private final FoodLogDAO foodLogDAO;
    private int activeUserId;

    private enum MealType {
        ЗАВТРАК, ОБЕД, УЖИН, ПЕРЕКУС, ДРУГОЕ
    }

    public LogFoodController(FoodLogDAO foodLogDAO) {
        this.foodLogDAO = foodLogDAO;
    }

    public void initializeData(int userId, LocalDate initialDate) {
        this.activeUserId = userId;
        mealTypeComboBox.getItems().addAll(Arrays.stream(MealType.values()).map(Enum::name).toList());
        mealTypeComboBox.getSelectionModel().select(MealType.УЖИН.name());
        logDatePicker.setValue(initialDate);
        logTimeField.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
        proteinPer100gField.setText("0.0");
        fatsPer100gField.setText("0.0");
        carbsPer100gField.setText("0.0");
        caloriesPer100gField.textProperty().addListener((obs, old, nev) -> calculateAndDisplayTotal());
        portionSizeField.textProperty().addListener((obs, old, nev) -> calculateAndDisplayTotal());
    }

    private void calculateAndDisplayTotal() {
        try {
            String calsText = caloriesPer100gField.getText().replaceAll(",", ".");
            String portionText = portionSizeField.getText().replaceAll(",", ".");
            int calsPer100g = Integer.parseInt(calsText);
            int portionGrams = Integer.parseInt(portionText);

            if (calsPer100g < 0 || portionGrams < 0) {
                throw new NumberFormatException("Числа должны быть положительными.");
            }

            FoodLog tempLog = new FoodLog(
                    activeUserId, "temp", "temp", calsPer100g,
                    BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, portionGrams
            );

            int totalCalories = tempLog.calculateTotalCalories();
            totalCaloriesLabel.setText(String.format("Итого калорий: %d ккал", totalCalories));
        } catch (NumberFormatException e) {
            totalCaloriesLabel.setText("Итого калорий: Ошибка ввода");
        } catch (Exception e) {
            totalCaloriesLabel.setText("Итого калорий: N/A");
        }
    }

    @FXML
    private void handleSaveFoodLog() {
        try {
            String description = descriptionField.getText().trim();
            String mealType = mealTypeComboBox.getValue();

            if (description.isEmpty() || mealType == null) {
                showAlert(Alert.AlertType.ERROR, "Ошибка ввода", "Неверные данные",
                        "Пожалуйста, заполните описание и выберите тип приема пищи.");
                return;
            }

            String proteinText = proteinPer100gField.getText().replaceAll(",", ".");
            String fatsText = fatsPer100gField.getText().replaceAll(",", ".");
            String carbsText = carbsPer100gField.getText().replaceAll(",", ".");
            int calsPer100g = Integer.parseInt(caloriesPer100gField.getText().replaceAll(",", "."));
            BigDecimal proteinPer100g = new BigDecimal(proteinText);
            BigDecimal fatsPer100g = new BigDecimal(fatsText);
            BigDecimal carbsPer100g = new BigDecimal(carbsText);
            int portionGrams = Integer.parseInt(portionSizeField.getText().replaceAll(",", "."));

            if (calsPer100g < 0 || portionGrams < 0 ||
                    proteinPer100g.floatValue() < 0 || fatsPer100g.floatValue() < 0 || carbsPer100g.floatValue() < 0) {
                showAlert(Alert.AlertType.ERROR, "Ошибка ввода", "Неверные данные",
                        "Все числовые поля (калории, БЖУ, порция) должны содержать неотрицательные числа.");
                return;
            }

            LocalDate date = logDatePicker.getValue();
            LocalTime time = LocalTime.parse(logTimeField.getText());
            Timestamp logTimestamp = Timestamp.valueOf(date.atTime(time));

            FoodLog newLog = new FoodLog(
                    activeUserId, "temp", "temp", calsPer100g,
                    BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, portionGrams
            );

            foodLogDAO.insertFoodLog(newLog);

            showAlert(Alert.AlertType.INFORMATION, "Успех", "Запись сохранена",
                    String.format("Запись '%s' (Итого: %d ккал) успешно добавлена.",
                            description, newLog.calculateTotalCalories()));

            handleCancel();
        } catch (NumberFormatException | DateTimeParseException e) {
            showAlert(Alert.AlertType.ERROR, "Ошибка ввода", "Неверный формат данных",
                    "Пожалуйста, проверьте поля калорий, БЖУ, порции и время (формат времени: ЧЧ:мм). Они должны быть числами.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Ошибка сохранения",
                    "Не удалось сохранить запись: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) descriptionField.getScene().getWindow();
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
