package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class DemoLauncher {
    public static void main(String[] args) {
        Application.launch(DemoLoader.class, args);
    }

    public static class DemoLoader extends Application {
        @Override
        public void start(Stage primaryStage) throws Exception {
            URL formXml = getClass().getResource("/demo.fxml");
            Parent root = new FXMLLoader(formXml).load();
            Scene scene = new Scene(root);
            primaryStage.setTitle("DemoLoader");
            primaryStage.setScene(scene);
            primaryStage.show();
        }
    }
}
