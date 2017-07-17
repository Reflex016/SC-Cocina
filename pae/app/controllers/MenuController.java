package pae.app.controllers;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import pae.alimentos.controllers.AbastecerController;
import pae.alimentos.controllers.ConsumirController;
import pae.alimentos.controllers.InventarioController;

public class MenuController implements Initializable {

    @FXML JFXButton homeButton;
    @FXML JFXButton abastecerButton;
    @FXML JFXButton consumirButton;
    @FXML JFXButton inventarioButton;
    @FXML JFXButton perfilButton;
    @FXML Label nombreUsuarioLabel;

    @Override
    public void initialize(URL url, ResourceBundle rb){
        nombreUsuarioLabel.setText(CurrentUser.currentUserFullName);
    }

    public void mostrarHome(ActionEvent event) throws Exception {

        HomeController homeController = new HomeController();
        homeController.init();
        homeController.start((Stage) ((Node) event.getSource()).getScene().getWindow());
    }

    public void mostrarAbastecer(ActionEvent event) throws Exception {

        AbastecerController abastecerController = new AbastecerController();
        abastecerController.init();
        abastecerController.start((Stage) ((Node) event.getSource()).getScene().getWindow());
    }

    public void mostrarConsumir(ActionEvent event) throws Exception {

        ConsumirController consumirController = new ConsumirController();
        consumirController.init();
        consumirController.start((Stage) ((Node) event.getSource()).getScene().getWindow());
    }

    public void mostrarInventario(ActionEvent event) throws Exception {

        InventarioController inventarioController = new InventarioController();
        inventarioController.init();
        inventarioController.start((Stage) ((Node) event.getSource()).getScene().getWindow());
    }

    public void mostrarAyuda() throws IOException {
        File file = new File("C:\\Users\\Isrrael\\IdeaProjects\\SC-Cocina\\src\\pae\\utils\\help\\Home.html");
        Desktop.getDesktop().browse(file.toURI());
    }

    public void mostrarPerfil(ActionEvent event) throws Exception {

        PerfilController perfilController = new PerfilController();
        perfilController.init();
        perfilController.start((Stage) ((Node) event.getSource()).getScene().getWindow());
    }

    public void salir(ActionEvent event) throws Exception {

        System.exit(0);
    }
}