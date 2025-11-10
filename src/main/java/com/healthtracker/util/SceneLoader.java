package com.healthtracker.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class SceneLoader {

    public record FXMLResult(Parent root, Object controller) {}

    public static FXMLResult loadScene(String fxmlFileName) throws IOException {

        String fxmlPath = "/com/healthtracker/app/" + fxmlFileName;


        URL location = SceneLoader.class.getResource(fxmlPath);

        if (location == null) {
            throw new IOException("FXML-ресурс не найден по пути: " + fxmlPath);
        }

        FXMLLoader loader = new FXMLLoader(location);
        Parent root = loader.load();
        Object controller = loader.getController();
        return new FXMLResult(root, controller);
    }

    public static void switchToScene(Node sourceNode, Parent newRoot, String title) {
        Stage stage = (Stage) sourceNode.getScene().getWindow();
        Scene scene = new Scene(newRoot);
        stage.setScene(scene);
        stage.setTitle(title);
        stage.show();
    }

    public static void switchToScene(Parent currentRoot, Parent newRoot, String title) {
        Stage stage = (Stage) currentRoot.getScene().getWindow();
        Scene scene = new Scene(newRoot);
        stage.setScene(scene);
        stage.setTitle(title);
        stage.show();
    }
}