package pae.alimentos.controllers;

import com.jfoenix.controls.*;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;

import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import pae.alimentos.dbconnections.InventarioDbAdapter;
import pae.alimentos.models.Inventario;
import pae.app.controllers.AlertMaker;
import pae.dbconnections.DbException;
import pae.dbconnections.PostgresDbConnection;
import pae.alimentos.dbconnections.AlimentoDbAdapter;
import pae.alimentos.models.Alimento;

public class AbastecerController extends Application implements Initializable{

    @FXML private JFXTextField insumoTextField;
    @FXML private JFXTextField anadirTextField;
    @FXML private JFXTextField cantidadTextField;
    @FXML private JFXToggleButton tipoToggle;
    @FXML private JFXComboBox<String> anadirComboBox = new JFXComboBox<String>();
    @FXML private JFXButton anadirButton;
    @FXML private JFXButton crearButton;
    @FXML private Label tipoLabel;
    @FXML private JFXHamburger menu;
    @FXML private JFXDrawer side;
    @FXML StackPane stackPane;

    static String db = "Alimentos_AndresBello";
    static String user = "postgres";
    static String pass = "1234";
    static String url = "jdbc:postgresql://localhost:5432/" + db;
    static PostgresDbConnection conn;
    private List<Alimento> alimentos;

    @Override
    public void start(Stage primaryStage) throws DbException, IOException {

        conn = new PostgresDbConnection (url, db, user, pass);
        conn.open(true);

        Parent abastecer = FXMLLoader.load(getClass().getResource("/pae/alimentos/views/AbastecerView.fxml"));
        Scene abastecer_scene = new Scene(abastecer);
        primaryStage.setScene(abastecer_scene);
        primaryStage.show();
    }


    @Override
    public void initialize (URL location, ResourceBundle resource) {

        mostrarMenu();
        AlimentoDbAdapter ad = new AlimentoDbAdapter();
        alimentos = ad.getList(conn, null);

        if (getNames(alimentos).size() > 0) {

            anadirComboBox.getItems().removeAll(anadirComboBox.getItems());
            anadirComboBox.getItems().addAll(getNames(alimentos));
            anadirComboBox.getSelectionModel().select(0);

            tipoLabel.setText(alimentos.get(anadirComboBox.getSelectionModel().getSelectedIndex()).getDescripcion());

            anadirComboBox.setOnAction(e -> {
                tipoLabel.setText(
                        alimentos.get(anadirComboBox.getSelectionModel().getSelectedIndex()).getDescripcion());
            });
        }
    }

    private List<String> getNames(List<Alimento> alimentos) {

        List<String> aNames = new ArrayList<>();
        for (Alimento a: alimentos){
            aNames.add(a.getNombre());
        }
        return  aNames;
    }

    public void crearInsumo(ActionEvent event) {

        if (insumoTextField.getText().isEmpty() || cantidadTextField.getText().isEmpty()) {

            AlertMaker alertMaker = new AlertMaker();
            alertMaker.loadInfoDialog(stackPane,
                    "Crear Insumos",
                    "Insumo no creado. Por favor, llene todos los campos.");
        }

        else {

            if (verifyInsumo(insumoTextField.getText())) {

                AlimentoDbAdapter ad = new AlimentoDbAdapter();
                String tipo = "";
                if (tipoToggle.isSelected()) {
                    tipo = "Litros";
                }
                else {
                    tipo = "Kilos";
                }
                Alimento alim = new Alimento(
                        insumoTextField.getText(), Double.parseDouble(cantidadTextField.getText()), tipo);
                ad.insertRecord(conn, alim, null);

                anadirComboBox.getItems().addAll(alim.getNombre());
                alimentos.add(alim);

                InventarioDbAdapter inventarioDbAdapter = new InventarioDbAdapter();
                int id_insumo = ad.getIdInsumo(conn,null,insumoTextField.getText());
                LocalDate now = LocalDate.now();
                String fecha = String.valueOf(now.getYear()) + "-"
                        + String.valueOf(now.getMonthValue()) + "-"
                        + String.valueOf(now.getDayOfMonth());
                Inventario inventario = new Inventario(id_insumo,"Crear",Double.parseDouble(cantidadTextField.getText()),fecha);
                inventarioDbAdapter.insertRecord(conn,inventario,null);

                AlertMaker alertMaker = new AlertMaker();
                alertMaker.loadInfoDialog(stackPane,
                        "Crear Insumos",
                        "La creación del insumo se ha realizado satisfactoriamente.");
                clearFields();
            }
            else {

                AlertMaker alertMaker = new AlertMaker();
                alertMaker.loadInfoDialog(stackPane,
                        "Crear Insumos",
                        "El nombre del insumo ya existe. Por favor elija otro nombre.");
                clearFields();
            }
        }
    }

    public void anadirInsumo(ActionEvent event) {

        String nombre = anadirComboBox.getSelectionModel().getSelectedItem();

        if (anadirTextField.getText().isEmpty()){

            AlertMaker alertMaker = new AlertMaker();
            alertMaker.loadInfoDialog(stackPane,
                    "Abastecer Insumos",
                    "Insumo no actualizado, debe introducir la cantidad a añadir.");
        }
        else {

            double cantidad = Double.parseDouble(anadirTextField.getText());
            int indice = anadirComboBox.getSelectionModel().getSelectedIndex();
            AlimentoDbAdapter ad = new AlimentoDbAdapter();
            double cantidad_disponible = 0.0;

            if(indice>=0) {

                if(cantidad>0) {

                    Alimento alimento=new Alimento(nombre,cantidad,"");
                    cantidad_disponible = ad.getCantidad(conn,null,alimento);
                    String insumoActual = String.valueOf(anadirComboBox.getSelectionModel().getSelectedItem());
                    ad.updateInsumo(conn,alimento,null,cantidad_disponible);
                    alimentos=ad.getList(conn,null);

                    AlertMaker alertMaker = new AlertMaker();
                    alertMaker.loadInfoDialog(stackPane,
                            "Abastecer Insumos",
                            "Se ha actualizado la cantidad disponible de "+alimento.getNombre()+"");
                    anadirTextField.setText("");

                    InventarioDbAdapter inventarioDbAdapter = new InventarioDbAdapter();
                    int id_insumo = ad.getIdInsumo(conn,null,insumoActual);

                    LocalDate now = LocalDate.now();
                    String fecha = String.valueOf(now.getYear()) + "-"
                            + String.valueOf(now.getMonthValue()) + "-"
                            + String.valueOf(now.getDayOfMonth());
                    Inventario inventario = new Inventario(id_insumo,"Abastecer",cantidad,fecha);
                    inventarioDbAdapter.insertRecord(conn,inventario,null);
                }
            }
        }
    }

    public void clearFields() {

        insumoTextField.setText("");
        cantidadTextField.setText("");
    }

    public boolean verifyInsumo(String insumo) {

        AlimentoDbAdapter alimentoDbAdapter = new AlimentoDbAdapter();
        Alimento alimento = new Alimento();

        if ((alimentoDbAdapter.getInsumoName(conn,alimento,insumo)).size() <= 0) { return true; }
        else { return  false;}
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