package com.healthtracker.app;

import com.healthtracker.init.DatabaseInitializer;
import com.healthtracker.util.DBConfig;
import com.healthtracker.util.SceneLoader;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainGUI extends Application {

    private static final String LOGIN_VIEW_FXML = "LoginView.fxml";

    @Override
    public void start(Stage primaryStage) {
        DatabaseInitializer.initialise();

        try {
            SceneLoader.FXMLResult result = SceneLoader.loadScene(LOGIN_VIEW_FXML);
            Parent root = result.root();

            Scene scene = new Scene(root);
            primaryStage.setTitle("Health Tracker - Вход");
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (Exception e) {
            System.err.println("Ошибка при запуске приложения: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
