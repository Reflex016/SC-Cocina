package pae.app.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

import java.net.URL;

import java.util.ResourceBundle;



import pae.alimentos.controllers.AbastecerController;
import pae.alimentos.controllers.ConsumirController;
import pae.alimentos.controllers.InventarioController;
import pae.dbconnections.DbException;

public class HomeController extends Application implements Initializable{

    @FXML JFXHamburger menu;
    @FXML JFXDrawer side;
    @FXML JFXButton abastecerButton;
    @FXML JFXButton consumirButton;
    @FXML JFXButton inventarioButton;

    @Override
    public void start(Stage primaryStage) throws DbException, IOException {

        Parent home = FXMLLoader.load(getClass().getResource("/pae/app/views/HomeView.fxml"));
        Scene home_scene = new Scene(home);
        primaryStage.setScene(home_scene);
        primaryStage.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        mostrarMenu();
    }

    public void mostrarMenu() {

        try {

            VBox box = FXMLLoader.load(getClass().getResource("/pae/app/views/MenuView.fxml"));
            side.setSidePane(box);
        }
        catch (IOException e) {

            e.printStackTrace();
        }

        HamburgerBackArrowBasicTransition transition = new HamburgerBackArrowBasicTransition(menu);
        transition.setRate(-1);
        menu.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, (event -> {
            transition.setRate(transition.getRate()*-1);
            transition.play();
            if (side.isShown()) side.close();
            else side.open();
        }));
    }

    public void mostrarAbastecer(ActionEvent event) throws Exception {

        AbastecerController comedorController = new AbastecerController();
        comedorController.init();
        comedorController.start((Stage) ((Node) event.getSource()).getScene().getWindow());
    }

    public void mostrarConsumir(ActionEvent event) throws Exception {

        ConsumirController consumirController = new ConsumirController();
        consumirController.init();
        consumirController.start((Stage) ((Node) event.getSource()).getScene().getWindow());
    }

    public void mostrarInventario(ActionEvent event) throws Exception {

        InventarioController estadisticasController = new InventarioController();
        estadisticasController.init();
        estadisticasController.start((Stage) ((Node) event.getSource()).getScene().getWindow());
    }
}