package pae.app.controllers;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {

        Parent root = FXMLLoader.load(getClass().getResource("/pae/app/views/LoginView.fxml"));
        primaryStage.setTitle("Sistema de Control de Cocina");
        primaryStage.setResizable(false);
        primaryStage.getIcons().add(new Image("/pae/utils/img/cocinalogo.png"));
        primaryStage.setScene(new Scene(root, 790, 590));
        primaryStage.show();
    }

    public static void main(String[] args) { launch(args); }
}
