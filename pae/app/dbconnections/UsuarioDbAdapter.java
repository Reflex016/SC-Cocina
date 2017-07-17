package pae.app.dbconnections;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import pae.app.models.Usuario;
import pae.dbconnections.DbException;
import pae.dbconnections.IDbConnection;
import pae.dbconnections.PostgresDbConnection;

import static pae.app.controllers.CurrentUser.currentUserEmail;
import static pae.app.controllers.CurrentUser.currentUserName;

public class UsuarioDbAdapter implements pae.app.dbconnections.IDataAdapter<Usuario> {
    //flag to drive open/close connection in local methods
    boolean localOpen;

    @Override
    public List<Usuario> getList(IDbConnection db,
                                 HashMap<String, Object> options) {
        List<Usuario> list = new ArrayList<>();
        PostgresDbConnection postgresDb = (PostgresDbConnection) db;

        try {
            if (!postgresDb.isOpen()){
                postgresDb.open(true);
                localOpen = true;
            }

            String sql = "SELECT * FROM public.users WHERE 1=1";

            if (options != null){
                String order = (String) options.get("order");
                if (order != null)
                    sql += " ORDER BY " + order;
            }

            ResultSet rs = postgresDb.getResultSet(sql);
            while (rs.next()){
                Usuario user = new Usuario(rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("pass"),
                        rs.getString("fullname"));
                list.add(user);
            }
        } catch (DbException | SQLException ex) {
            Logger.getLogger(pae.app.dbconnections.UsuarioDbAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (localOpen){
            localOpen = false;
            postgresDb.close();
        }
        return list;
    }

    @Override
    public Usuario getRecord(IDbConnection db, HashMap<String, Object> options) {
        List<Usuario> list = getList(db, options);
        if (list.size()>0) return list.get(0);
        return null;
    }

    @Override
    public boolean insertRecord(IDbConnection db, Usuario record, HashMap<String, Object> options) {
        PostgresDbConnection postgresDb = (PostgresDbConnection) db;
        int insertedRecords = 0;

        try {
            if (!postgresDb.isOpen()){
                postgresDb.open(false);
                localOpen = true;
            }

            String sql = "INSERT INTO public.users (username,email,pass,fullname)"
                    + " VALUES ('"
                    + record.getUserName() + "', '"
                    + record.getEmail() + "', '"
                    + record.getPassword() + "', '"
                    + record.getFullName() + "');";
            System.out.println("INSERT SQL:" + sql);

            insertedRecords = postgresDb.executeUpdate(sql);

        } catch (DbException | SQLException ex) {
            Logger.getLogger(pae.app.dbconnections.UsuarioDbAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (localOpen){
            localOpen = false;
            postgresDb.close();
        }

        return (insertedRecords>0);
    }

    @Override
    public boolean updateRecord(IDbConnection db, Usuario record, HashMap<String, Object> options) {
        PostgresDbConnection postgresDb = (PostgresDbConnection) db;
        int updatedRecords = 0;

        try {
            if (!postgresDb.isOpen()){
                postgresDb.open(false);
                localOpen = true;
            }

            String sql = "UPDATE public.users "
                    + "SET username = '" + record.getUserName()
                    + "' SET email = '" + record.getEmail()
                    + "' SET pass = '" + record.getPassword()
                    + "' WHERE pass = '" + record.getFullName() + "';";

            System.out.println("UPDATE SQL:" + sql);

            updatedRecords = postgresDb.executeUpdate(sql);
        } catch (DbException | SQLException ex) {
            Logger.getLogger(pae.app.dbconnections.UsuarioDbAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (localOpen){
            localOpen = false;
            postgresDb.close();
        }

        return (updatedRecords>0);
    }

    public boolean updateMail(IDbConnection db, Usuario record, HashMap<String, Object> options) {
        PostgresDbConnection postgresDb = (PostgresDbConnection) db;
        int updatedRecords = 0;

        try {
            if (!postgresDb.isOpen()){
                postgresDb.open(false);
                localOpen = true;
            }

            String sql = "UPDATE public.users "
                    + "SET email = '" + record.getEmail() + "' WHERE email = '"+currentUserEmail+"'";

            System.out.println("UPDATE SQL:" + sql);

            updatedRecords = postgresDb.executeUpdate(sql);
        } catch (DbException | SQLException ex) {
            Logger.getLogger(pae.app.dbconnections.UsuarioDbAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (localOpen){
            localOpen = false;
            postgresDb.close();
        }

        return (updatedRecords>0);
    }

    public boolean updateUser(IDbConnection db, Usuario record, HashMap<String, Object> options) {
        PostgresDbConnection postgresDb = (PostgresDbConnection) db;
        int updatedRecords = 0;

        try {
            if (!postgresDb.isOpen()){
                postgresDb.open(false);
                localOpen = true;
            }

            String sql = "UPDATE public.users "
                    + "SET username = '" + record.getUserName() + "' WHERE username = '"+currentUserName+"'";

            System.out.println("UPDATE SQL:" + sql);

            updatedRecords = postgresDb.executeUpdate(sql);
        } catch (DbException | SQLException ex) {
            Logger.getLogger(pae.app.dbconnections.UsuarioDbAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (localOpen){
            localOpen = false;
            postgresDb.close();
        }

        return (updatedRecords>0);
    }

    public boolean updatePassword(IDbConnection db, Usuario record, HashMap<String, Object> options) {
        PostgresDbConnection postgresDb = (PostgresDbConnection) db;
        int updatedRecords = 0;

        try {
            if (!postgresDb.isOpen()){
                postgresDb.open(false);
                localOpen = true;
            }

            String sql = "UPDATE public.users "
                    + " SET pass = '" + record.getPassword()
                    + "' WHERE pass = '" + record.getFullName() + "';";

            System.out.println("UPDATE SQL:" + sql);

            updatedRecords = postgresDb.executeUpdate(sql);
        } catch (DbException | SQLException ex) {
            Logger.getLogger(pae.app.dbconnections.UsuarioDbAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (localOpen){
            localOpen = false;
            postgresDb.close();
        }

        return (updatedRecords>0);
    }

    @Override
    public boolean deleteRecord(IDbConnection db, Usuario record, HashMap<String, Object> options) {
        PostgresDbConnection postgresDb = (PostgresDbConnection) db;
        int deletedRecords = 0;

        try {
            if (!postgresDb.isOpen()){
                postgresDb.open(false);
                localOpen = true;
            }

            String sql = "DELETE FROM public.users "
                    + "WHERE pass = '" + record.getPassword() + "'";

            System.out.println("DELETE SQL:" + sql);

            deletedRecords = postgresDb.executeUpdate(sql);
        } catch (DbException | SQLException ex) {
            Logger.getLogger(pae.app.dbconnections.UsuarioDbAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (localOpen){
            localOpen = false;
            postgresDb.close();
        }

        return (deletedRecords > 0);
    }

    public List<Usuario> getUserLogin(IDbConnection db,
                                      HashMap<String, Object> options, String usuario, String contrasena) {
        List<Usuario> list = new ArrayList<>();
        PostgresDbConnection postgresDb = (PostgresDbConnection) db;

        try {
            if (!postgresDb.isOpen()){
                postgresDb.open(true);
                localOpen = true;
            }

            String sql = "SELECT * FROM public.users WHERE users.username = '"+usuario+"' AND users.pass = '"+contrasena+"';";

            if (options != null){
                String order = (String) options.get("order");
                if (order != null)
                    sql += " ORDER BY " + order;
            }

            ResultSet rs = postgresDb.getResultSet(sql);
            while (rs.next()){
                Usuario user = new Usuario(rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("pass"),
                        rs.getString("fullname"));
                list.add(user);
            }
        } catch (DbException | SQLException ex) {
            Logger.getLogger(pae.app.dbconnections.UsuarioDbAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (localOpen){
            localOpen = false;
            postgresDb.close();
        }
        return list;
    }

    public List<Usuario> getComfirmName(IDbConnection db,
                                      HashMap<String, Object> options, String nombre) {
        List<Usuario> list = new ArrayList<>();
        PostgresDbConnection postgresDb = (PostgresDbConnection) db;

        try {
            if (!postgresDb.isOpen()){
                postgresDb.open(true);
                localOpen = true;
            }

            String sql = "SELECT * FROM public.users WHERE users.username = '"+nombre+"';";

            if (options != null){
                String order = (String) options.get("order");
                if (order != null)
                    sql += " ORDER BY " + order;
            }

            ResultSet rs = postgresDb.getResultSet(sql);
            while (rs.next()){
                Usuario user = new Usuario(rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("pass"),
                        rs.getString("fullname"));
                list.add(user);
            }
        } catch (DbException | SQLException ex) {
            Logger.getLogger(pae.app.dbconnections.UsuarioDbAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (localOpen){
            localOpen = false;
            postgresDb.close();
        }
        return list;
    }
}