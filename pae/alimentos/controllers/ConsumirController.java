package pae.alimentos.controllers;

import com.jfoenix.controls.*;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;

import javafx.application.Application;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import pae.alimentos.dbconnections.AlimentoDbAdapter;
import pae.alimentos.dbconnections.InventarioDbAdapter;
import pae.alimentos.models.Alimento;
import pae.alimentos.models.Inventario;
import pae.app.controllers.AlertMaker;
import pae.dbconnections.DbException;
import pae.dbconnections.PostgresDbConnection;

public class ConsumirController extends Application implements Initializable{

    @FXML private JFXButton consumirButton;
    @FXML private JFXTextField cantidadTextField;
    @FXML private Label disponibleLabel;
    @FXML private Label tipoLabel;
    @FXML private JFXComboBox insumosComboBox;
    @FXML JFXHamburger menu;
    @FXML JFXDrawer side;
    @FXML StackPane stackPane;

    static String db = "Alimentos_AndresBello";
    static String user = "postgres";
    static String pass = "1234";
    static String url = "jdbc:postgresql://localhost:5432/" + db;
    static PostgresDbConnection conn;
    private List<Alimento> insumos;

    @Override
    public void start(Stage primaryStage) throws DbException, IOException {

        conn = new PostgresDbConnection (url, db, user, pass);
        conn.open(true);

        Parent consumir = FXMLLoader.load(getClass().getResource("/pae/alimentos/views/ConsumirView.fxml"));
        Scene consumir_scene = new Scene(consumir);
        primaryStage.setScene(consumir_scene);
        primaryStage.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        mostrarMenu();
        getComboBoxInfo();
    }

    public void getComboBoxInfo() {

        AlimentoDbAdapter alimentoDbAdapter = new AlimentoDbAdapter();
        insumos = alimentoDbAdapter.getList(conn,null);

        if (getNames(insumos).size() > 0){

            insumosComboBox.getItems().removeAll(insumosComboBox.getItems());
            insumosComboBox.getItems().addAll(getNames(insumos));
            insumosComboBox.getSelectionModel().select(0);

            disponibleLabel.setText(Double.toString(
                    insumos.get(insumosComboBox.getSelectionModel().getSelectedIndex()).getCantidad()));
            tipoLabel.setText(insumos.get(insumosComboBox.getSelectionModel().getSelectedIndex()).getDescripcion());

            insumosComboBox.setOnAction((Event e) -> {
                disponibleLabel.setText(getCantDisp());
                tipoLabel.setText(getTipoDisp());
            });
        }
    }

    private String getCantDisp ()
    {
        if (insumosComboBox.getSelectionModel().getSelectedIndex() >= 0)
            return Double.toString(insumos.get(insumosComboBox.getSelectionModel().getSelectedIndex()).getCantidad());
        return "";
    }

    private String getTipoDisp ()
    {
        if (insumosComboBox.getSelectionModel().getSelectedIndex() >= 0)
            return insumos.get(insumosComboBox.getSelectionModel().getSelectedIndex()).getDescripcion();
        return "";
    }

    public void usarInsumo(ActionEvent event) {

        if (Double.parseDouble(cantidadTextField.getText()) > Double.parseDouble(disponibleLabel.getText())) {

            AlertMaker alertMaker = new AlertMaker();
            alertMaker.loadInfoDialog(stackPane,
                    "Consumir Insumos",
                    "Por favor verifique su pedido, este excede el total dispoible.");
        }
        else {

            AlimentoDbAdapter alimentoDbAdapter = new AlimentoDbAdapter();
            InventarioDbAdapter inventarioDbAdapter = new InventarioDbAdapter();
            String insumoActual = String.valueOf(insumosComboBox.getSelectionModel().getSelectedItem());
            int id_insumo = alimentoDbAdapter.getIdInsumo(conn,null,insumoActual);
            Alimento actual = new Alimento(insumoActual,0.0,"");

            Double cantidadConsumida = Double.parseDouble(cantidadTextField.getText());
            Double cantidadActual = alimentoDbAdapter.getCantidad(conn,null,actual);

            LocalDate now = LocalDate.now();
            String fecha = String.valueOf(now.getYear()) + "-"
                    + String.valueOf(now.getMonthValue()) + "-"
                    + String.valueOf(now.getDayOfMonth());
            Inventario inventario = new Inventario(id_insumo,"Consumir",cantidadConsumida,fecha);

            alimentoDbAdapter.updateInsumo(conn,actual,null,cantidadActual-cantidadConsumida);
            inventarioDbAdapter.insertRecord(conn,inventario,null);

            AlertMaker alertMaker = new AlertMaker();
            alertMaker.loadInfoDialog(stackPane,
                    "Consumir Insumos",
                    "Su pedido se ha procesado de manera exitosa.");

            getComboBoxInfo();
            cantidadTextField.setText("");
        }
    }

    private List<String> getNames(List<Alimento> alimentos) {

        List<String> aNames = new ArrayList<>();
        for (Alimento a: alimentos){
            aNames.add(a.getNombre());
        }
        return  aNames;
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
}