package pae.alimentos.controllers;

import com.jfoenix.controls.*;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import net.sf.dynamicreports.report.exception.DRException;
import pae.alimentos.dbconnections.AlimentoDbAdapter;
import pae.alimentos.models.Alimento;
import pae.app.controllers.AlertMaker;
import pae.dbconnections.DbException;
import pae.dbconnections.PostgresDbConnection;

public class InventarioController extends Application implements Initializable{

    @FXML private JFXComboBox insumosComboBox;
    @FXML private Label disponibleLabel;
    @FXML private Label tipoLabel;
    @FXML private JFXButton eliminarButton;
    @FXML private JFXButton inventarioButton;
    @FXML private JFXButton reporteButton;
    @FXML private JFXDatePicker inicialDatePicker;
    @FXML private JFXDatePicker finalDatePicker;
    @FXML private JFXHamburger menu;
    @FXML private JFXDrawer side;
    @FXML StackPane stackPane;
    @FXML private JFXSpinner inventarioSpinner;
    @FXML private JFXSpinner reporteSpinner;
    @FXML private JFXTabPane pane;

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

        Parent inventario = FXMLLoader.load(getClass().getResource("/pae/alimentos/views/InventarioView.fxml"));
        Scene inventario_scene = new Scene(inventario);
        primaryStage.setScene(inventario_scene);
        primaryStage.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources){

        mostrarMenu();
        getComboBoxInfo();
        inventarioSpinner.setVisible(false);
        reporteSpinner.setVisible(false);
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

            insumosComboBox.setOnAction(e -> {
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

    public void deleteInsumo(ActionEvent event) {

        AlimentoDbAdapter alimentoDbAdapter = new AlimentoDbAdapter();
        String insumo = String.valueOf(insumosComboBox.getSelectionModel().getSelectedItem());
        Alimento actual = new Alimento(insumo,0.0,"");
        alimentoDbAdapter.deleteRecord(conn,actual,null);

        AlertMaker alertMaker = new AlertMaker();
        alertMaker.loadInfoDialog(stackPane,"Eliminar Insumo","El insumo ha sido eliminado satisfactoriamente.");
        getComboBoxInfo();

    }

    public void showInventario() throws DRException {

        inventarioSpinner.setVisible(true);
        AlertMaker alertMaker = new AlertMaker();
        alertMaker.loadInfoDialog(stackPane,"Generando Reporte","Por favor, espere mientras se genera el reporte solicitado.");
        disableAll();
        InventarioThread inventarioThread = new InventarioThread();
        inventarioThread.start();
    }

    public class InventarioThread extends Thread {

        public void run() {
            Pdf pdf = new Pdf();
            try {
                pdf.buildInventario();
            } catch (DRException e) {
                e.printStackTrace();
            }
            inventarioSpinner.setVisible(false);
            enableAll();
        }
    }

    public void showReport() throws DRException {

        if (inicialDatePicker.getValue() != null && finalDatePicker.getValue() != null) {

            if (inicialDatePicker.getValue().isBefore(finalDatePicker.getValue())) {

                reporteSpinner.setVisible(true);
                AlertMaker alertMaker = new AlertMaker();
                alertMaker.loadInfoDialog(stackPane,"Generando Reporte","Por favor, espere mientras se genera el reporte solicitado.");
                disableAll();
                ReportThread reportThread = new ReportThread();
                reportThread.start();
            }
            else {

                AlertMaker alertMaker = new AlertMaker();
                alertMaker.loadInfoDialog(stackPane,
                        "Reporte de Inventario",
                        "La fecha inicial debe ser anterior a la fecha final.");
            }
        }
        else {

            AlertMaker alertMaker = new AlertMaker();
            alertMaker.loadInfoDialog(stackPane,
                    "Reporte de Inventario",
                    "Por favor, ingrese ambas fechas para generar el reporte");
        }
    }

    public class ReportThread extends Thread {

        String fechaInicial = String.valueOf(inicialDatePicker.getValue());
        String fechaFinal = String.valueOf(finalDatePicker.getValue());

        public void run() {
            Pdf pdf = new Pdf();
            try {
                pdf.buildReport(fechaInicial, fechaFinal);
            } catch (DRException e) {
                e.printStackTrace();
            }
            reporteSpinner.setVisible(false);
            enableAll();
        }
    }

    public void disableAll(){
        reporteButton.setDisable(true);
        inventarioButton.setDisable(true);
        menu.setDisable(true);
        insumosComboBox.setDisable(true);
        eliminarButton.setDisable(true);
        inicialDatePicker.setDisable(true);
        finalDatePicker.setDisable(true);
        pane.setDisable(true);
    }

    public void enableAll(){
        reporteButton.setDisable(false);
        inventarioButton.setDisable(false);
        menu.setDisable(false);
        insumosComboBox.setDisable(false);
        eliminarButton.setDisable(false);
        inicialDatePicker.setDisable(false);
        finalDatePicker.setDisable(false);
        pane.setDisable(false);
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

    private List<String> getNames(List<Alimento> alimentos) {

        List<String> aNames = new ArrayList<>();
        for (Alimento a: alimentos){
            aNames.add(a.getNombre());
        }
        return  aNames;
    }
}