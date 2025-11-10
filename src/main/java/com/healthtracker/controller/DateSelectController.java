package com.healthtracker.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;

import java.sql.Date;
import java.time.LocalDate;
import java.util.function.Consumer;

public class DateSelectController {

    private final Date initialDate;
    private final Consumer<Date> dateUpdateCallback;

    @FXML private DatePicker datePicker;
    @FXML private Button selectButton;

    public DateSelectController(Date initialDate, Consumer<Date> dateUpdateCallback) {
        this.initialDate = initialDate;
        this.dateUpdateCallback = dateUpdateCallback;
    }

    @FXML
    public void initialize() {
        if (initialDate != null) {
            datePicker.setValue(initialDate.toLocalDate());
        } else {
            datePicker.setValue(LocalDate.now());
        }
        selectButton.setStyle("-fx-background-color: #2a62ff; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
    }

    @FXML
    private void handleSelectDate() {
        LocalDate selectedLocalDate = datePicker.getValue();
        if (selectedLocalDate != null) {
            Date newDate = Date.valueOf(selectedLocalDate);
            dateUpdateCallback.accept(newDate);
            closeWindow();
        } else {
            System.err.println("Ошибка: Дата не выбрана.");
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        if (datePicker != null) {
            Stage stage = (Stage) datePicker.getScene().getWindow();
            stage.close();
        }
    }
}
