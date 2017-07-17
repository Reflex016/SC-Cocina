package pae.app.models;

import java.util.HashMap;

public class Usuario extends HashMap<String, Object> {

    private String userName;
    private String email;
    private String password;
    private String fullName;

    public Usuario(String userName, String email, String password, String fullName) {
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.fullName = fullName;
    }

    public Usuario(){

    }

    public void setUserName() {

    }

    public String getUserName() {
        return userName;
    }

    public void setEmail() {

    }

    public String getEmail() {
        return email;
    }

    public void setPassword() {

    }

    public String getPassword() {
        return password;
    }

    public void setFullName() {

    }

    public String getFullName() {
        return fullName;
    }

    @Override
    public String toString() {

        String usuario = "User Name: " + getUserName() +
                " E-Mail: " + getEmail() + " Password: " + getPassword() + " Full Name: " + getFullName();
        return usuario;
    }
}