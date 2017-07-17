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

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import pae.app.models.Usuario;
import pae.app.dbconnections.UsuarioDbAdapter;
import pae.dbconnections.PostgresDbConnection;


public class LoginController extends Application implements Initializable {

    static String db = "Alimentos_AndresBello";
    static String user = "postgres";
    static String pass = "1234";
    static String url = "jdbc:postgresql://localhost:5432/" + db;
    static PostgresDbConnection conn;

    private List<Usuario> usuarios;
    private String usuario = "";
    private String contrasena = "";

    @FXML private JFXButton ingresarButton;
    @FXML private JFXTextField usuarioTextField;
    @FXML private JFXPasswordField contrasenaPasswordField;
    @FXML private JFXButton registrarseButton;
    @FXML private JFXButton salirButton;
    @FXML StackPane stackPane;

    @Override
    public void start(Stage primaryStage) throws Exception {

        Parent login = FXMLLoader.load(getClass().getResource("/pae/app/views/LoginView.fxml"));
        Scene login_scene = new Scene(login);
        primaryStage.setScene(login_scene);
        primaryStage.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    void loginPae(ActionEvent event) throws Exception {

        conn = new PostgresDbConnection(url, db, user, pass);
        conn.open(true);

        if (makeLogin()) {

            HomeController homeController = new HomeController();
            homeController.init();
            homeController.start((Stage) ((Node) event.getSource()).getScene().getWindow());
        }
        else {

            AlertMaker alertMaker = new AlertMaker();
            alertMaker.loadInfoDialog(stackPane,"Inicio de Sesión","Verifique sus datos. Usuario o contraseña incorrectos");
        }
    }

    private boolean makeLogin() {

        usuario = usuarioTextField.getText();
        contrasena = contrasenaPasswordField.getText();
        UsuarioDbAdapter usuarioDbAdapter = new UsuarioDbAdapter();
        usuarios = usuarioDbAdapter.getUserLogin(conn,null,usuario,contrasena);

        if (usuarios.size() > 0) {

            if ((usuarios.get(0).getUserName().equals(usuario)) && (usuarios.get(0).getPassword().equals(contrasena))) {

                CurrentUser.currentUserName = usuarios.get(0).getUserName();
                CurrentUser.currentUserEmail = usuarios.get(0).getEmail();
                CurrentUser.currentUserPassword = usuarios.get(0).getPassword();
                CurrentUser.currentUserFullName = usuarios.get(0).getFullName();
                return true;
            }
        }
        return false;
    }

    public void mostrarRegistro(ActionEvent event) throws Exception {

        RegistroController registroController = new RegistroController();
        registroController.init();
        registroController.start((Stage) ((Node) event.getSource()).getScene().getWindow());
    }

    public void mostrarAyuda(ActionEvent event) throws Exception {
        MenuController menuController = new MenuController();
        menuController.mostrarAyuda();
    }

    public void salir() throws Exception {

        System.exit(0);
    }
}