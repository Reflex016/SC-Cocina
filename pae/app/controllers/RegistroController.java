package pae.app.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import pae.app.dbconnections.UsuarioDbAdapter;
import pae.dbconnections.DbException;
import pae.app.models.Usuario;
import pae.dbconnections.PostgresDbConnection;

public class RegistroController extends Application implements Initializable {

    @FXML JFXButton volverButton;
    @FXML JFXButton registrarButton;
    @FXML JFXTextField usuarioTextField;
    @FXML JFXTextField nombreTextField;
    @FXML JFXTextField correoTextField;
    @FXML JFXPasswordField contrasenaPasswordField;
    @FXML JFXPasswordField confirmarPasswordField;
    @FXML StackPane stackPane;

    static String db = "Alimentos_AndresBello";
    static String user = "postgres";
    static String pass = "1234";
    static String url = "jdbc:postgresql://localhost:5432/" + db;
    static PostgresDbConnection conn;

    @Override
    public void start(Stage primaryStage) throws DbException, IOException {

        conn = new PostgresDbConnection (url, db, user, pass);
        conn.open(true);

        Parent register = FXMLLoader.load(getClass().getResource("/pae/app/views/RegistroView.fxml"));
        Scene register_scene = new Scene(register);
        primaryStage.setScene(register_scene);
        primaryStage.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void mostrarLogin(ActionEvent event) throws Exception {

        LoginController loginController = new LoginController();
        loginController.init();
        loginController.start((Stage) ((Node) event.getSource()).getScene().getWindow());
    }

    public void addUser() throws DbException, IOException {

        String usuario = usuarioTextField.getText();
        String nombre = nombreTextField.getText();
        String correo = correoTextField.getText();
        String contrasena = contrasenaPasswordField.getText();
        String confirmar = confirmarPasswordField.getText();

        if (usuario.isEmpty() || nombre.isEmpty() || correo.isEmpty() || contrasena.isEmpty() || confirmar.isEmpty()) {

            AlertMaker alertMaker = new AlertMaker();
            alertMaker.loadInfoDialog(stackPane,
                    "Registro de Usuario",
                    "Por favor, llene todos los campos para completar el registro.");
        }
        else {

            if(verifyUser(usuario)) {

                if(contrasena.equals (confirmar)) {

                    Usuario nuevo = new Usuario(usuario, correo, contrasena, nombre);
                    UsuarioDbAdapter usuarioDbAdapter = new UsuarioDbAdapter();
                    usuarioDbAdapter.insertRecord(conn, nuevo, null);

                    AlertMaker alertMaker = new AlertMaker();
                    alertMaker.loadInfoDialog(stackPane,
                            "Registro de Usuario",
                            "Felicidades! Ha sido registrado satisfactoriamente. Ya puede iniciar sesión.");
                }
                else {

                    AlertMaker alertMaker = new AlertMaker();
                    alertMaker.loadInfoDialog(stackPane,
                            "Registro de Usuario",
                            "Disculpe, las contraseñas con coinciden. Por favor, verifique.");
                }
            }
            else {
                AlertMaker alertMaker = new AlertMaker();
                alertMaker.loadInfoDialog(stackPane,
                        "Registro de Usuario",
                        "Disculpe, el nombre de usuario ya existe. Por favor, intente con otro nombre de usuario.");
            }
        }
    }

    public boolean verifyUser(String nombreUsuario) {

        UsuarioDbAdapter usuarioDbAdapter = new UsuarioDbAdapter();
        Usuario usuario = new Usuario();

        if ((usuarioDbAdapter.getComfirmName(conn,usuario,nombreUsuario)).size() <= 0) { return true; }
        else { return  false;}
    }
}