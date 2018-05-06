package com.littledemo;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import javax.xml.stream.Location;
import java.io.IOException;
import java.net.URL;

public class MainWindow extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {


        URL url = getClass().getResource("/com.littledemo/MainWindow.fxml");

        Parent root = FXMLLoader.load(url);

        primaryStage.setTitle("NasWaterMarker");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

    }


}
