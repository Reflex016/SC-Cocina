package pae.app.controllers;

import com.jfoenix.controls.*;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import javafx.application.Application;
import javafx.fxml.FXML;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import pae.app.dbconnections.UsuarioDbAdapter;
import pae.app.models.Usuario;
import pae.dbconnections.DbException;
import pae.dbconnections.PostgresDbConnection;

import static pae.app.controllers.CurrentUser.*;

public class PerfilController extends Application implements Initializable{

    @FXML private JFXHamburger menu;
    @FXML private JFXDrawer side;
    @FXML private JFXTextField nombreTextField;
    @FXML private JFXTextField correoTextField;
    @FXML private JFXButton usuarioButton;
    @FXML private JFXPasswordField actualPasswordField;
    @FXML private JFXPasswordField nuevaPasswordField;
    @FXML private JFXButton contrasenaButton;
    @FXML private StackPane stackPane;

    private List<Usuario> usuarios;

    static String db = "Alimentos_AndresBello";
    static String user = "postgres";
    static String pass = "1234";
    static String url = "jdbc:postgresql://localhost:5432/" + db;
    static PostgresDbConnection conn;

    @Override
    public void initialize(URL location, ResourceBundle resources){

        mostrarMenu();
        nombreTextField.setText(CurrentUser.currentUserName);
        correoTextField.setText(CurrentUser.currentUserEmail);
    }

    @Override
    public void start(Stage primaryStage) throws DbException, IOException {

        conn = new PostgresDbConnection (url, db, user, pass);
        conn.open(true);

        Parent perfil = FXMLLoader.load(getClass().getResource("/pae/app/views/PerfilView.fxml"));
        Scene perfil_scene = new Scene(perfil);
        primaryStage.setScene(perfil_scene);
        primaryStage.show();
    }

    public void updateUser() {

        String nombre = nombreTextField.getText();
        String correo = correoTextField.getText();

        if (!nombre.isEmpty() && !correo.isEmpty()) {

            UsuarioDbAdapter usuarioDbAdapter = new UsuarioDbAdapter();

            if (nombre.equals(currentUserName) && !correo.equals(currentUserEmail)) {

                Usuario usuario = new Usuario(nombre,correo,currentUserPassword,currentUserFullName);
                usuarioDbAdapter.updateMail(conn, usuario, null);
                currentUserEmail = correo;

                AlertMaker alertMaker = new AlertMaker();
                alertMaker.loadInfoDialog(stackPane,
                        "Actualización de Correo",
                        "Su correo electrónico ha sido modificado exitosamente.");
            }
            else if (!nombre.equals(currentUserName) && correo.equals(currentUserEmail)) {

                usuarios = usuarioDbAdapter.getComfirmName(conn,null,nombre);

                if (usuarios.size() > 0) {

                    nombreTextField.setText(currentUserName);

                    AlertMaker alertMaker = new AlertMaker();
                    alertMaker.loadInfoDialog(stackPane,
                            "Actualización de Usuario",
                            "Disculpe, el nombre de usuario ya existe, por favor, intente con un nuevo nombre de usuario");
                }
                else {

                    Usuario usuario = new Usuario(nombre,correo,currentUserPassword,currentUserFullName);
                    usuarioDbAdapter.updateUser(conn,usuario,null);
                    currentUserName = nombre;

                    AlertMaker alertMaker = new AlertMaker();
                    alertMaker.loadInfoDialog(stackPane,
                            "Actualización de Usuario",
                            "Su nombre de usuario ha sido modificado exitosamente.");
                }
            }
            else  if (!nombre.equals(currentUserName) && !correo.equals(currentUserEmail)) {

                Usuario usuario = new Usuario(nombre,correo,currentUserPassword,currentUserFullName);
                usuarioDbAdapter.updateUser(conn,usuario,null);
                usuarioDbAdapter.updateMail(conn,usuario,null);
                currentUserName = nombre;
                currentUserEmail = correo;

                AlertMaker alertMaker = new AlertMaker();
                alertMaker.loadInfoDialog(stackPane,
                        "Actualización de Usuario y Correo",
                        "Su nombre de usuario y correo electrónico han sido modificados exitosamente.");
            }
        }
        else {

            AlertMaker alertMaker = new AlertMaker();
            alertMaker.loadInfoDialog(stackPane,
                    "Actualización de Usuario y Correo",
                    "Disculpe, ninguno de los campos debe estar vacío.");

            nombreTextField.setText(currentUserName);
            correoTextField.setText(currentUserEmail);
        }
    }

    public void updatePassword() {

        String actual = actualPasswordField.getText();
        String nueva = nuevaPasswordField.getText();

        if (actual.equals(currentUserPassword)) {

            UsuarioDbAdapter usuarioDbAdapter = new UsuarioDbAdapter();
            Usuario usuario = new Usuario(currentUserName, currentUserEmail, nueva, actual);
            usuarioDbAdapter.updatePassword(conn,usuario,null);

            AlertMaker alertMaker = new AlertMaker();
            alertMaker.loadInfoDialog(stackPane,
                    "Actualización de Contraseña",
                    "Su contraseña se ha sido modificado exitosamente.");

            currentUserPassword = nueva;
            actualPasswordField.setText("");
            nuevaPasswordField.setText("");
        }
        else {

            AlertMaker alertMaker = new AlertMaker();
            alertMaker.loadInfoDialog(stackPane,
                    "Actualización de Contraseña",
                    "Disculpe, la contraseña es no es válida. Por favor, verifique sus datos.");

            actualPasswordField.setText("");
            nuevaPasswordField.setText("");
        }
    }

    public void mostrarMenu(){

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