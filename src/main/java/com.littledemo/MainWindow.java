package com.littledemo;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;
import java.net.URL;

public class MainWindow extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {


        URL url = getClass().getResource("/assets/MainWindow.fxml");

        Parent root = FXMLLoader.load(url);

        primaryStage.setTitle("NAS加密信息交易");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

    }


}
